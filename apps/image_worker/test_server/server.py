import asyncio
from aiohttp import web


WORKER_ID = "123"
PASSWORD = "456"


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

        self.host = "0.0.0.0"
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


    async def stop(self) -> None:

        for ws in self.websocket_clients.copy():
            if not ws.closed:
                await ws.close()
                self.websocket_clients.discard(ws)

        self.websocket_clients.clear()

        if self.runner:
            await self.runner.cleanup()

    async def handle_login(
        self,
        request: web.Request
    ) -> web.Response:

        body = await request.json()
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

        # try:

        #     inital_message = await ws.receive()
        #     assert inital_message

        #     task = prepare_task()
        #     assert task
            
        #     task_message = ormsgpack.packb(task.model_dump())
            
        #     await ws.send_bytes(task_message)
            
        #     task_result= await ws.receive()
            
        #     assert task_result


        # finally:
        #     self.websocket_clients.discard(ws)

        return ws
    


# async def workflow():

#     server = Fakeserver()

#     await server.start()

#     # try:
#     #     worker = ImageWorker(
#     #         "123",
#     #         "456",
#     #         server.http_url,
#     #         server.ws_url
#     #     )

#     #     await worker.run()
       
#     # finally:
#     #     await server.stop()



# def test_worker():
#     asyncio.run(workflow())