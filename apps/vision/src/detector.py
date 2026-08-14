from pathlib import Path

import cv2
import numpy as np
from cv2.typing import MatLike
from ultralytics import YOLO


class Detector:
    def __init__(
        self,
        od_enabled: bool = False,
        od_model_path: str = "yolov8n.pt",
        seg_model_path: str = "yolov8n-seg.pt",
    ):
        """
        Initialize the detector.
        mode: "segmentation" (just use segmentation model) or "both" (OD for crops, then segmentation)
        """
        self.od_enabled = od_enabled
        if self.od_enabled:
            self.od_model = YOLO(od_model_path)
        self.seg_model = YOLO(seg_model_path)

    def process(self, img: MatLike) -> list[dict]:
        """
        Processes an image and returns a list of detected objects with their full-image masks.
        """
        if self.od_enabled:
            return self._process_with_od(img)

        return self._process_segment_only(img)

    def _process_with_od(self, img: MatLike) -> list[dict]:
        # Class 0 is person in YOLO (follow COCO format)
        h_orig, w_orig = img.shape[:2]
        detected_objects = []

        for box in self.od_model(img, classes=[0])[0].boxes:
            # Making sure that its not out of bound
            x1, y1, x2, y2 = map(int, box.xyxy[0].tolist())
            x1, y1 = max(0, x1), max(0, y1)
            x2, y2 = min(w_orig, x2), min(h_orig, y2)

            # Extraact that region only
            crop = img[y1:y2, x1:x2]
            if crop.size == 0:
                continue

            crop_h, crop_w = crop.shape[:2]
            crop_pass = self.seg_model(crop)[0]

            if crop_pass.masks is not None:
                for c_box, mask_tensor in zip(crop_pass.boxes, crop_pass.masks.data):
                    cls_id = int(c_box.cls[0])
                    conf = float(c_box.conf[0])
                    label_name = self.seg_model.names[cls_id]

                    # NOTE: segmenation mask are usually repr as prob tensors
                    # Convert mask to numpy array then a boolean mask
                    mask_np = mask_tensor.cpu().numpy()
                    mask_resized = cv2.resize(mask_np, (crop_w, crop_h)) > 0.5

                    # Project the mask back to its original image resolution
                    full_mask = np.zeros((h_orig, w_orig), dtype=bool)
                    full_mask[y1:y2, x1:x2] = mask_resized

                    # Convert from 2D tensor to simple interger list
                    proj_x1, proj_y1, proj_x2, proj_y2 = map(
                        int, c_box.xyxy[0].tolist()
                    )

                    detected_objects.append(
                        {
                            "mask": full_mask,
                            "class_id": cls_id,
                            "conf": conf,
                            "label": label_name,
                            "bbox": (
                                x1 + proj_x1,
                                y1 + proj_y1,
                                x1 + proj_x2,
                                y1 + proj_y2,
                            ),
                        }
                    )
                return detected_objects

    def _process_segment_only(self, img: MatLike) -> list[dict]:
        h_orig, w_orig = img.shape[:2]
        detected_objects = []

        results = self.seg_model(img)[0]
        if results.masks is not None:
            for box, mask_tensor in zip(results.boxes, results.masks.data):
                cls_id = int(box.cls[0])
                conf = float(box.conf[0])
                label_name = self.seg_model.names[cls_id]

                mask_np = mask_tensor.cpu().numpy()
                mask_resized = cv2.resize(mask_np, (w_orig, h_orig)) > 0.5

                x1, y1, x2, y2 = map(int, box.xyxy[0].tolist())

                detected_objects.append(
                    {
                        "mask": mask_resized,
                        "class_id": cls_id,
                        "conf": conf,
                        "label": label_name,
                        "bbox": (x1, y1, x2, y2),
                    }
                )

        return detected_objects

    def is_hit(
        self, detected_objects: list[dict], shape_mask: np.ndarray
    ) -> list[dict]:
        """
        Determines if a given shape mask hits the segmentation masks.

        Args:
            detected_objects: List of objects returned by process().
            shape_mask: 2D boolean numpy array representing the shape's mask (True for shape pixels) with the same resolution as the image.

        Returns:
            list of dicts containing hit information for each object that was hit.
            [{'object_index': int, 'hit_percentage': float, 'label': str}, ...]
        """
        hit_results = []

        # Early stop in case there's nothing in mask
        shape_area = np.sum(shape_mask)
        if shape_area == 0:
            return hit_results

        for idx, obj in enumerate(detected_objects):
            obj_mask = obj["mask"]

            # Bitmap logical and to determine intersections
            intersection = np.logical_and(shape_mask, obj_mask)
            intersection_area = np.sum(intersection)

            if intersection_area > 0:
                hit_percentage = (intersection_area / shape_area) * 100.0
                hit_results.append(
                    {
                        "object_index": idx,
                        "label": obj["label"],
                        "hit_percentage": hit_percentage,
                        "intersection_area": float(intersection_area),
                        "shape_area": float(shape_area),
                    }
                )

        return hit_results


if __name__ == "__main__":
    BASE = Path(__file__).parent.parent
    IMG = (
        BASE
        / "dataset/train/images"
        / "GH010721_1684740561494_frame00232_jpg.rf.49db53299c453cc158cc73c7499d8a48.jpg"
    )

    if IMG.exists():
        detector = Detector(od_enabled=True)

        print("Processing image...")
        img = cv2.imread(str(IMG))
        if img is not None:
            objects = detector.process(img)
            print(f"Detected {len(objects)} objects.")

            # Example hit test: a circle with radius 10 in the middle of the image
            h, w = img.shape[:2]
            shape_mask = np.zeros((h, w), dtype=bool)
            center_x, center_y = w // 2, h // 2

            # Create the circle mask using cv2
            RADIUS = 10
            circle_canvas = np.zeros((h, w), dtype=np.uint8)
            cv2.circle(circle_canvas, (center_x, center_y), RADIUS, 255, -1)
            # Bitmap of tensor where circle is placed
            shape_mask = circle_canvas > 0

            hits = detector.is_hit(objects, shape_mask)
            print("Hit Results:", hits)

            overlay = img.copy()

            # NOTE: the 0.5x multiply is to set transparency to 50%
            for obj in objects:
                seg_mask = obj["mask"]
                overlay[seg_mask] = (overlay[seg_mask] + np.array([0, 255, 0])) * 0.5
                intersection = np.logical_and(shape_mask, seg_mask)
                overlay[intersection] = (
                    overlay[intersection] + np.array([0, 0, 255])
                ) * 0.5

            # Shows hitbox of mask shapes that were not overlapped
            pure_hitbox = (
                np.logical_and(
                    shape_mask, ~np.any([obj["mask"] for obj in objects], axis=0)
                )
                if objects
                else shape_mask
            )
            overlay[pure_hitbox] = (overlay[pure_hitbox] + np.array([255, 0, 0])) * 0.5

            # Display the result
            cv2.imshow("Debug Overlay", overlay.astype(np.uint8))
            cv2.waitKey(0)
            cv2.destroyAllWindows()

        else:
            print("Could not load image.")
    else:
        print(f"Image not found at {IMG}")
