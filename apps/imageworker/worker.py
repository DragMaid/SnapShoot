import numpy as np
import cv2
from apps.vision.src.detector import Detector
from gateway_client import GatewayClient
from pydantic import BaseModel

THRESHOLD = 0.5

class WorkerMessage(BaseModel):
    task_id: str = ''
    session_id: str = ''
    success: bool = False


class ImageWorker:
    '''Carry out the task received from the gateway client'''

    def __init__(self, worker_id: str, password: str, gateway: GatewayClient, threshold: int = THRESHOLD):

        self.gateway = GatewayClient(worker_id=worker_id, password=password)
        self.threshold = threshold
        self.detector = Detector()

    async def run(self):
        '''Receive the task continously'''

        await self.gateway.connect()

        while True:
            self.gateway.send(WorkerMessage())

            task = await self.gateway.receive()

            self.handle_task(task)

    async def handle_task(self, task: dict):

        result = await self.process_image(task)

        await self.gateway.send(result)


    def process_image(self, data: dict):
        '''Process the image based on the json received'''
        success = self.is_hit(self.reconstruct_image())
        result = WorkerMessage(task_id = data['task_id'],
                               session_id = data['session_id'],
                               success = success)
        return result


    def reconstruct_image(self, image_data: bytes):
        """Restore the original image"""
        image_array = np.frombuffer(image_data, dtype=np.uint8)

        image = cv2.imdecode(
            image_array,
            cv2.IMREAD_COLOR
        )
        return image


    def is_hit(self, image) -> bool:
        '''Determines if the shot hits based on hit percentage'''
        h, w = image[:2]
        center_x, center_y = w // 2, h // 2
        circle_canvas = np.zeros((w, h), dtype=np.unit8)
        cv2.circle(circle_canvas, (center_x, center_y), self.radius, 255, -1)
        shape_mask = circle_canvas > 0

        detected_obj = self.detector.process(image)
        hit_percentage = self.detector.is_hit(detected_objects=detected_obj, shape_mask=shape_mask)['hit_percentage']

        if hit_percentage >= self.threshold:
            return True
        return False
