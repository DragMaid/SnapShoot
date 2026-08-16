from pathlib import Path

import cv2
import numpy as np
import pytest

from detector import Detector

BASE = Path(__file__).parent.parent
IMG_PATH = BASE / "assets/example.jpg"


@pytest.fixture(scope="module")
def detector() -> Detector:
    return Detector(od_enabled=False)


@pytest.fixture(scope="module")
def image() -> np.ndarray:
    if not IMG_PATH.exists():
        pytest.skip(f"Test image not found at {IMG_PATH}")
    img = cv2.imread(str(IMG_PATH))
    if img is None:
        pytest.skip(f"Could not load image at {IMG_PATH}")
    return img


@pytest.fixture(scope="module")
def objects(detector: Detector, image: np.ndarray) -> list[dict]:
    return detector.process(image)


def circle_mask(h: int, w: int, radius: int = 10) -> np.ndarray:
    """Boolean mask of a filled circle centered in an h x w frame."""
    canvas = np.zeros((h, w), dtype=np.uint8)
    cv2.circle(canvas, (w // 2, h // 2), radius, 255, -1)
    return canvas > 0


def test_process_returns_well_formed_objects(image: np.ndarray, objects: list[dict]):
    assert isinstance(objects, list)

    for obj in objects:
        assert set(obj) >= {"mask", "class_id", "conf", "label", "bbox"}
        assert obj["mask"].dtype == bool
        assert obj["mask"].shape == image.shape[:2]
        assert 0.0 <= obj["conf"] <= 1.0
        assert len(obj["bbox"]) == 4


def test_is_hit_reports_only_real_intersections(
    detector: Detector, image: np.ndarray, objects: list[dict]
):
    h, w = image.shape[:2]
    shape_mask = circle_mask(h, w, radius=10)

    hits = detector.is_hit(objects, shape_mask)

    assert isinstance(hits, list)
    for hit in hits:
        assert 0 <= hit["object_index"] < len(objects)
        assert hit["label"] == objects[hit["object_index"]]["label"]
        assert 0.0 < hit["hit_percentage"] <= 100.0

        obj_mask = objects[hit["object_index"]]["mask"]
        intersection_area = np.sum(np.logical_and(shape_mask, obj_mask))
        assert intersection_area == hit["intersection_area"]
        assert intersection_area > 0


def test_is_hit_empty_shape_mask_returns_no_hits(
    detector: Detector, image: np.ndarray, objects: list[dict]
):
    h, w = image.shape[:2]
    empty_mask = np.zeros((h, w), dtype=bool)

    assert detector.is_hit(objects, empty_mask) == []


def test_overlay_composition_does_not_raise(image: np.ndarray, objects: list[dict]):
    """Regression test for the debug-overlay math in detector.__main__."""
    h, w = image.shape[:2]
    shape_mask = circle_mask(h, w, radius=10)

    overlay = image.copy()
    for obj in objects:
        seg_mask = obj["mask"]
        overlay[seg_mask] = (overlay[seg_mask] + np.array([0, 255, 0])) * 0.5
        intersection = np.logical_and(shape_mask, seg_mask)
        overlay[intersection] = (overlay[intersection] + np.array([0, 0, 255])) * 0.5

    pure_hitbox = (
        np.logical_and(shape_mask, ~np.any([obj["mask"] for obj in objects], axis=0))
        if objects
        else shape_mask
    )
    overlay[pure_hitbox] = (overlay[pure_hitbox] + np.array([255, 0, 0])) * 0.5

    assert overlay.astype(np.uint8).shape == image.shape
