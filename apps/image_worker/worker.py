import numpy as np
import cv2
from apps.vision.src.detector import Detector
from apps.image_worker.client import GatewayClient
from pydantic import BaseModel, Field
from aiohttp import WSMsgType
import ormsgpack
import aiohttp
from enum import Enum

THRESHOLD = 0.5
MAX_RETRY = 10

class MessageType(Enum):
    READY = 'ready'
    RESULT = 'result'

class WorkerMessage(BaseModel):
    message_type: MessageType = MessageType.READY
    task_id: str | None = None
    success: bool | None = None

class TaskMessage(BaseModel):
    task_id: str
    session_id: str
    image_data: bytes
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

    async def run(self):
        '''Receive the task continously'''
        authenticated = False
        connected = False
        for i in range(MAX_RETRY):
            try:
                await self.gateway.authenticate()
                authenticated = True
                break    
            except ConnectionRefusedError as e:
                print(e)
                raise
        if not authenticated:
            raise RuntimeError("Could not authenticated the worker")
        
        for i in range(MAX_RETRY):
            try:
                await self.gateway.connect()
                connected = True
                break
            
            except aiohttp.client_exceptions.ClientConnectorError as e:
                print(e)
                raise
        if not connected:
            raise RuntimeError("Could not connect with websocket gateway")
        
        initial_message = WorkerMessage()

        await self.gateway.send(initial_message)
        
        while True:

            message = await self.gateway.receive()

            
            if message.type == WSMsgType.BINARY:

                task = ormsgpack.unpackb(message.data)

                assert task

                result = await self.handle_task(task)
                
                assert result
                
                await self.gateway.send(result)
                            
            
            elif message.type == WSMsgType.CLOSE:
                break

            elif message.type == WSMsgType.CLOSED:
                break
 
            elif message.type == WSMsgType.ERROR:
                print("WebSocket error:", self.websocket.exception())
                break
            
            

    async def handle_task(self, task: dict):
        
        task = TaskMessage.model_validate(task)

        result = self.process_image(task)
    
        return result


    def process_image(self, task: TaskMessage):
        '''Process the image based on the json received'''
        success = self.is_hit(self.reconstruct_image(task.image_data), task.radius)
        result = WorkerMessage(MessageType=MessageType.RESULT,
                                task_id = task.task_id,
                               success = success)
        return result


    def reconstruct_image(self, image_data: str):
        """Restore the original image"""

        image_array = np.frombuffer(
            image_data,
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
        hit_result = self.detector.is_hit(detected_objects=detected_obj, shape_mask=shape_mask)
        if hit_result:
            hit_percentage = hit_result[0]['hit_percentage']

            if hit_percentage >= self.threshold:
                return True
        return False

