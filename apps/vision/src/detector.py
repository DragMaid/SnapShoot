from pathlib import Path

import cv2
import numpy as np
from ultralytics import YOLO

BASE = Path(__file__).parent.parent
IMG = (
    BASE
    / "dataset/train/images"
    / "GH010721_1684740561494_frame00232_jpg.rf.49db53299c453cc158cc73c7499d8a48.jpg"
)

# Initialize models
model = YOLO("yolo26n.pt")
seg_model = YOLO("yolo26n-seg.pt")

# Read original image using OpenCV
orig_img = cv2.imread(str(IMG))
if orig_img is None:
    raise FileNotFoundError(f"Image not found at path: {IMG}")

h_orig, w_orig = orig_img.shape[:2]
annotated_img = orig_img.copy()

# Step 1: Detect primary objects (e.g., humans)
first_pass = model(orig_img, classes=[0])[0]

for i, box in enumerate(first_pass.boxes):
    box = first_pass.boxes[i]
    # Extract bounding box
    x1, y1, x2, y2 = map(int, box.xyxy[0].tolist())

    # Clamp coordinates to valid image bounds
    x1, y1 = max(0, x1), max(0, y1)
    x2, y2 = min(w_orig, x2), min(h_orig, y2)

    # Crop the detected region
    crop = orig_img[y1:y2, x1:x2]
    if crop.size == 0:
        continue

    crop_h, crop_w = crop.shape[:2]

    # Draw the initial crop bounding box (Blue)
    cv2.rectangle(annotated_img, (x1, y1), (x2, y2), (255, 0, 0), 2)
    cv2.putText(
        annotated_img,
        f"Human #{i + 1}",
        (x1, max(y1 - 10, 15)),
        cv2.FONT_HERSHEY_SIMPLEX,
        0.5,
        (255, 0, 0),
        2,
    )

    # Step 2: Pass crop through the segmentation model
    crop_pass = seg_model(crop)[0]

    # Step 3: Extract and map masks back to original image coordinates
    if crop_pass.masks is not None:
        for j, (c_box, mask_tensor) in enumerate(
            zip(crop_pass.boxes, crop_pass.masks.data)
        ):
            cls_id = int(c_box.cls[0])
            conf = float(c_box.conf[0])
            label_name = seg_model.names[cls_id]

            # Convert segmentation mask tensor to numpy and resize to match crop dimensions
            mask_np = mask_tensor.cpu().numpy()
            mask_resized = cv2.resize(mask_np, (crop_w, crop_h)) > 0.5

            # Define mask color (Green overlay)
            color = np.array([0, 255, 0], dtype=np.uint8)

            # Extract ROI from annotated image corresponding to the original crop
            roi = annotated_img[y1:y2, x1:x2]

            # Blend mask onto ROI (Alpha blending for transparency)
            alpha = 0.4
            colored_roi = roi.copy()
            colored_roi[mask_resized] = color

            # Apply blended mask back onto the original annotated image
            cv2.addWeighted(colored_roi, alpha, roi, 1 - alpha, 0, roi)

            # Plot segmentation bounding boxes projected back to full image space
            cx1, cy1, cx2, cy2 = map(int, c_box.xyxy[0].tolist())
            proj_x1, proj_y1 = x1 + cx1, y1 + cy1
            proj_x2, proj_y2 = x1 + cx2, y1 + cy2

            label = f"{label_name} {conf:.2f}"
            cv2.rectangle(
                annotated_img, (proj_x1, proj_y1), (proj_x2, proj_y2), (0, 255, 0), 1
            )
            cv2.putText(
                annotated_img,
                label,
                (proj_x1, max(proj_y1 - 5, 15)),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.4,
                (0, 255, 0),
                1,
            )

# Display final output
cv2.imshow("Projected Crop Segmentations", annotated_img)
cv2.waitKey(0)
cv2.destroyAllWindows()
