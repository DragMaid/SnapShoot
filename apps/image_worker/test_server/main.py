import asyncio
from server import Fakeserver
import signal

async def main():
    server = Fakeserver()
    
    await server.start()
    
    stop_event = asyncio.Event()
    loop = asyncio.get_running_loop()
    for sig in (signal.SIGTERM, signal.SIGINT):
        loop.add_signal_handler(sig, stop_event.set)

    await stop_event.wait()
    await server.stop()



if __name__ == "__main__":
    asyncio.run(main())