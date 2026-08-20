import websockets
import asyncio
import requests

HTTPENDPOINT = ''
WEBSOCKETENDPOINT = ''


def HTTPConnect(worker_id: str, password: str) -> str:
    '''Authenticate the worker in first connection'''
    response = requests.post(HTTPENDPOINT, 
                             json={'worker_id': worker_id,
                                 'password': password})
    assert response.status_code == 200
    access_token = response.json()['access_token']
    return access_token


class GatewayClient:
    '''Initiate the websocket connection with the gateway'''

    def __init__(self, worker_id: str, password: str, uri: str = WEBSOCKETENDPOINT):
        self.token = HTTPConnect(worker_id, password)
        self.websocket = None

    async def connect(self):
        headers = {
            "Authorization": f'Bearer {self.token}'
        }

        self.websocket = await websockets.connect(
            self.uri,
            additional_header = headers,
        )
        # logger.info

    async def receive(self) -> dict:
        await self.websocket.receive_json()

    async def send(self, data: dict):
        await self.websocket.send_json(data)

    async def close(self):
        if self.websocket:
            self.websocket.close()
