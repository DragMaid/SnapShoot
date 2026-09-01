from worker import ImageWorker
import asyncio
import signal

async def main():
    worker = ImageWorker(
        '123',
        '456',
        'http://server:3000/register-worker',
        'ws://server:3000/ws'
    )
    await worker.run()
    
    stop_event = asyncio.Event()
    loop = asyncio.get_running_loop()
    for sig in (signal.SIGTERM, signal.SIGINT):
        loop.add_signal_handler(sig, stop_event.set)

    await stop_event.wait()
    await worker.stop()
    
    
    


if __name__ == "__main__":
    asyncio.run(main())
