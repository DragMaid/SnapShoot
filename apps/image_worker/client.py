import aiohttp
import ormsgpack

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
        self.session = None

    async def authenticate(self) -> None:
        '''Authenticate the worker in first connection'''
        self.session = aiohttp.ClientSession()
        async with self.session as session:
                
            async with session.post(
                self.http_endpoint, 
                json={'worker_id': self.worker_id, 'password': self.password},
                timeout=2) as response:
                data = await response.json()
                access_token = data['access_token']
                self.token = access_token
        await self.session.close()

    async def connect(self):
        self.session = aiohttp.ClientSession()
        self.websocket = await self.session.ws_connect(
            self.uri,
            headers={
                "Authorization": f"Bearer {self.token}"
            }
        )
        
    async def receive(self) -> dict:
        data = await self.websocket.receive()
        return data

    async def send(self, data):
        data = ormsgpack.packb(data.model_dump())
        await self.websocket.send_bytes(data)

    async def close(self):
        if self.websocket:
            self.websocket.close()
