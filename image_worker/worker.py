import numpy as np
import cv2
from apps.vision.src.detector import Detector
from .client import GatewayClient
from pydantic import BaseModel, Field
import base64

THRESHOLD = 0.5

class WorkerMessage(BaseModel):
    task_id: str = '1233'
    session_id: str = '22'
    success: bool = False

class TaskMessage(BaseModel):
    task_id: str
    session_id: str
    image_data: list[str]
    radius: float = Field(None, ge=0.00, le=1.00)

class ImageWorker:
    '''Carry out the task received from the gateway client'''
    
    def __init__(self, worker_id: str, password: str, 
                 http_endpoint: str, uri: str,
                 threshold: int = THRESHOLD ):

        self.gateway = GatewayClient(worker_id=worker_id, 
                                     password=password, 
                                     http_endpoint=http_endpoint, 
                                     uri=uri)
        self.threshold = threshold
        self.detector = Detector()
        print("Initialzing")

    async def run(self):
        '''Receive the task continously'''
        await self.gateway.authenticate()

        await self.gateway.connect()
        print("Connection started")
        
        initial_message = WorkerMessage()
        print("Initial message",initial_message)
        await self.gateway.send(initial_message)
        
        while True:
            
            task = await self.gateway.receive()
            print("Handling the task")

            await self.handle_task(task)
            print("Worker finished task")

    async def handle_task(self, task: dict):
        print("Validating the task")
        
        task = TaskMessage.model_validate(task)

        result = self.process_image(task)

        await self.gateway.send(result)


    def process_image(self, task: TaskMessage):
        '''Process the image based on the json received'''
        success = self.is_hit(self.reconstruct_image(task.image_data), task.radius)
        result = WorkerMessage(task_id = task.task_id,
                               session_id = task.session_id,
                               success = success)
        return result


    def reconstruct_image(self, image_data: str):
        """Restore the original image"""
        image_data = [
                base64.b64decode(image)
                for image in image_data
            ]
        image_bytes = b"".join(image_data)

        image_array = np.frombuffer(
            image_bytes,
            dtype=np.uint8
        )

        image = cv2.imdecode(
            image_array,
            cv2.IMREAD_COLOR
        )

        if image is None:
            raise ValueError("Failed to decode image")

        return image


    def is_hit(self, image, radius: float) -> bool:
        '''Determines if the shot hits based on hit percentage'''
        h, w = image.shape[:2]
        center_x, center_y = w // 2, h // 2
        circle_canvas = np.zeros((h, w), dtype=np.uint8)
        radius = int(min(w, h) * radius)
        cv2.circle(circle_canvas, (center_x, center_y), radius, 255, -1)
        shape_mask = circle_canvas > 0

        detected_obj = self.detector.process(image)
        hit_percentage = self.detector.is_hit(detected_objects=detected_obj, shape_mask=shape_mask)[0]['hit_percentage']

        if hit_percentage >= self.threshold:
            return True
        return False

