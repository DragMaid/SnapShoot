FROM python:3.13-slim

COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
    libxcb1 \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /project

COPY apps/image_worker/pyproject.toml apps/image_worker/uv.lock ./apps/image_worker/
COPY apps/vision/pyproject.toml ./apps/vision/pyproject.toml

WORKDIR /project/apps/image_worker
RUN uv sync --frozen --no-install-project --package image_worker

WORKDIR /project

COPY apps/vision/src/ ./apps/vision/src/
COPY apps/image_worker/ ./apps/image_worker/

WORKDIR /project/apps/image_worker
RUN uv sync --frozen

# Set the PYTHONPATH environment variable so Python finds the "apps" directory layout
ENV PYTHONPATH=/project

CMD [ "uv", "run", "main.py" ] 


