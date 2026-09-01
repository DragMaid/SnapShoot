FROM python:3.13-slim

COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

WORKDIR /server

COPY apps/image_worker/test_server .

RUN uv sync --frozen

CMD ["uv", "run", "main.py"]

