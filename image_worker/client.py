import websockets
import requests
import json
import asyncio
import aiohttp

class GatewayClient:
    '''Initiate the websocket connection with the gateway'''

    def __init__(
            self,
            worker_id: str,
            password: str,
            http_endpoint: str,
            uri: str):
        self.worker_id = worker_id
        self.password = password
        self.http_endpoint = http_endpoint
        self.uri = uri
        self.websocket = None
        self.session = aiohttp.ClientSession()

    async def authenticate(self) -> None:
        '''Authenticate the worker in first connection'''
        async with self.session as session:
                
            print(self.http_endpoint)
            async with session.post(
                self.http_endpoint, 
                json={'worker_id': self.worker_id, 'password': self.password},
                timeout=2) as response:
                data = await response.json()
                access_token = data['access_token']
                self.token = access_token

    async def connect(self):
        headers = {
            "Authorization": f'Bearer {self.token}'
        }

        self.websocket = await websockets.connect(
            self.uri,
            additional_headers = headers,
        )

    async def receive(self) -> dict:
        data = await self.websocket.recv()
        data = json.loads(data)
        return data

    async def send(self, data: dict):
        await self.websocket.send(data.model_dump_json())

    async def close(self):
        if self.websocket:
            self.websocket.close()
