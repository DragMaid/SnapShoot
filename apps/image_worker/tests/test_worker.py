import asyncio
import json
from aiohttp import web
import base64
from pathlib import Path
from image_worker.worker import ImageWorker, TaskMessage

'''Put a test image file containing a person for testing worker'''
BASE_FOLDER = Path(__file__).parent
WORKER_ID = "123"
PASSWORD = "456"
test_image = BASE_FOLDER / 'test_image.jpeg'


class Fakeserver:

    def __init__(self) -> None:
        self.app = web.Application()

        self.app.router.add_post(
            "/register-worker",
            self.handle_login
        )

        self.app.router.add_get(
            "/ws",
            self.handle_websocket
        )

        self.runner: web.AppRunner | None = None
        self.site: web.TCPSite | None = None

        self.host = "127.0.0.1"
        self.port = 3000

        self.http_url = (
            f"http://{self.host}:{self.port}/register-worker"
        )

        self.ws_url = (
            f"ws://{self.host}:{self.port}/ws"
        )

        self.websocket_clients = set()

    async def start(self) -> None:
        self.runner = web.AppRunner(self.app)

        await self.runner.setup()

        self.site = web.TCPSite(
            self.runner,
            self.host,
            self.port
        )

        await self.site.start()

        print(f"Fake server running at http://{self.host}:{self.port}")

    async def stop(self) -> None:

        for ws in self.websocket_clients.copy():
            if not ws.closed:
                await ws.close()
                self.websocket_clients.discard(ws)
                print("Close")

        self.websocket_clients.clear()

        if self.runner:
            await self.runner.cleanup()
        print("FInal")

    async def handle_login(
        self,
        request: web.Request
    ) -> web.Response:

        body = await request.json()
        print(body)
        worker_id = body.get("worker_id")
        password = body.get("password")

        if worker_id != WORKER_ID or password != PASSWORD:
            return web.json_response(
                {"error": "Invalid credentials"},
                status=401
            )

        return web.json_response({
            "access_token": "ACKJFNJKEFHSCUNJK"
        })

    async def handle_websocket(
        self,
        request: web.Request
    ) -> web.WebSocketResponse:

        ws = web.WebSocketResponse()

        await ws.prepare(request)

        self.websocket_clients.add(ws)

        try:
            message = await ws.receive()

            print("Received from worker:", message)
            
            task = prepare_task()
            
            await ws.send_json(task.model_dump())
            
            print("Successfully send task")
            
            message = await ws.receive()

            print("Received result from worker:", message.data)
            
            # async for message in ws:

            #     print("Received result from worker:", message.data)
            
            #     print("stoping the server")
            #     await self.stop()

        finally:
            print("Handler exiting")
            self.websocket_clients.discard(ws)
            self.stop()

        return ws
    

def prepare_task():
    with open(test_image, "rb") as image_file:
         
    # Read raw file data into a bytes object
        byte_data = image_file.read()
        
    # Separate it into chunks
        chunk_size = 4096

        byte_list = [byte_data[i:i + chunk_size]
            for i in range(0, len(byte_data), chunk_size)]
    
    # Encode the list of bytes into a list of str for serialization
        encoded_image = [
        base64.b64encode(chunk).decode("utf-8")
        for chunk in byte_list
        ]
    
    task = TaskMessage(
        task_id='12324', 
        session_id = '3212', 
        image_data = encoded_image, 
        radius=0.1
    )
    return task


async def workflow():

    server = Fakeserver()

    await server.start()

    try:
        worker = ImageWorker(
            "123",
            "456",
            server.http_url,
            server.ws_url
        )

        await worker.run()
       
    finally:
        await server.stop()



def test_worker():
    asyncio.run(workflow())