'use client';

import {
  useEffect,
  useRef,
  useState,
} from 'react';

import {
  useDeviceOrientation,
} from './useDeviceOrientation';

export default function TrackOrientation() {

  const {
    orientation,
    isSupported,
    isPermissionGranted,
    permissionError,
    requestPermission,
  } = useDeviceOrientation();

  const videoRef =
    useRef<HTMLVideoElement>(null);

  const [cameraEnabled, setCameraEnabled] =
    useState<boolean>(false);

  const [cameraError, setCameraError] =
    useState<string | null>(null);

  const [permissionStatus, setPermissionStatus] =
    useState<PermissionStatus | null>(null);

  useEffect(() => {
    async function checkCameraPermission() {
      if (
        typeof navigator === 'undefined'
      ) {
        return;
      }

      if (!navigator.permissions) {
        console.log(
          'Permissions API is not supported.'
        );

        return;
      }

      try {
        const status =
          await navigator.permissions.query({
            name: 'camera' as PermissionName,
          });

        console.log(
          'Initial camera permission:',
          status.state
        );

        setPermissionStatus(status);

        status.onchange = () => {
          console.log(
            'Camera permission changed:',
            status.state
          );

          setPermissionStatus(status);
        };
      } catch (error) {
        console.error(
          'Error checking camera permission:',
          error
        );
      }
    }

    checkCameraPermission();
  }, []);

  async function startCamera(): Promise<boolean> {
    try {
      setCameraError(null);

      if (
        !navigator.mediaDevices ||
        !navigator.mediaDevices.getUserMedia
      ) {
        setCameraError(
          'Camera API is not supported by this browser.'
        );

        return false;
      }

      console.log(
        'Requesting camera permission...'
      );
      const stream =
        await navigator.mediaDevices.getUserMedia({
          video: {
            facingMode: 'environment',

            width: {
              ideal: 1280,
            },

            height: {
              ideal: 720,
            },
          },

          audio: false,
        });

      console.log(
        'Camera permission granted.'
      );


      if (videoRef.current) {
        videoRef.current.srcObject = stream;

        await videoRef.current.play();
      }

      setCameraEnabled(true);

      if (navigator.permissions) {
        try {
          const status =
            await navigator.permissions.query({
              name: 'camera' as PermissionName,
            });

          setPermissionStatus(status);
        } catch (error) {
          console.log(
            'Could not refresh camera permission:',
            error
          );
        }
      }

      return true;
    } catch (error) {
      console.error(
        'Camera error:',
        error
      );

      if (
        error instanceof DOMException
      ) {
        if (
          error.name === 'NotAllowedError'
        ) {
          setCameraError(
            'Camera permission was denied.'
          );
        } else if (
          error.name === 'NotFoundError'
        ) {
          setCameraError(
            'No camera was found.'
          );
        } else if (
          error.name === 'NotReadableError'
        ) {
          setCameraError(
            'Camera is already being used by another application.'
          );
        } else {
          setCameraError(
            `${error.name}: ${error.message}`
          );
        }
      } else {
        setCameraError(
          'Unable to access the camera.'
        );
      }

      return false;
    }
  }


  async function startTracking() {
    console.log(
      '========== START TRACKING =========='
    );

    const orientationGranted =
      await requestPermission();

    console.log(
      'Orientation granted:',
      orientationGranted
    );

    if (!orientationGranted) {
      return;
    }
    const cameraStarted =
      await startCamera();

    console.log(
      'Camera started:',
      cameraStarted
    );

    console.log(
      '===================================='
    );
  }

  function stopCamera() {
    if (!videoRef.current) {
      return;
    }

    const stream =
      videoRef.current.srcObject as
        | MediaStream
        | null;

    if (stream) {
      stream.getTracks().forEach((track) => {
        track.stop();
      });
    }

    videoRef.current.srcObject = null;

    setCameraEnabled(false);
  }

  useEffect(() => {
    return () => {
      if (!videoRef.current) {
        return;
      }

      const stream =
        videoRef.current.srcObject as
          | MediaStream
          | null;

      if (stream) {
        stream.getTracks().forEach((track) => {
          track.stop();
        });
      }
    };
  }, []);

  return (
    <main
      style={{
        position: 'fixed',
        inset: 0,

        width: '100vw',
        height: '100vh',

        overflow: 'hidden',

        backgroundColor: 'black',
      }}
    >
      {/* ======================================
          CAMERA VIDEO
          ====================================== */}

      <video
        ref={videoRef}
        autoPlay
        playsInline
        muted
        style={{
          position: 'absolute',

          top: 0,
          left: 0,

          width: '100vw',
          height: '100vh',

          objectFit: 'cover',

          backgroundColor: 'black',

          zIndex: 0,
        }}
      />

      {/* ======================================
          START SCREEN
          ====================================== */}

      {!cameraEnabled && (
        <div
          style={{
            position: 'absolute',

            inset: 0,

            display: 'flex',

            flexDirection: 'column',

            justifyContent: 'center',

            alignItems: 'center',

            padding: '20px',

            color: 'white',

            backgroundColor:
              'rgba(0, 0, 0, 0.85)',

            zIndex: 10,
          }}
        >
          <h1>
            Sensor Workspace
          </h1>

          {/* ORIENTATION SUPPORT */}

          <p>
            <strong>
              Orientation support:
            </strong>{' '}
            {isSupported
              ? 'Yes'
              : 'No'}
          </p>

          {/* ORIENTATION PERMISSION */}

          <p>
            <strong>
              Sensor permission:
            </strong>{' '}
            {isPermissionGranted
              ? 'Granted'
              : 'Not granted'}
          </p>

          {/* CAMERA PERMISSION */}

          <p>
            <strong>
              Camera permission:
            </strong>{' '}
            {permissionStatus?.state ??
              'Unknown'}
          </p>

          {/* SENSOR ERROR */}

          {permissionError && (
            <p
              style={{
                color: 'red',
              }}
            >
              ❌ {permissionError}
            </p>
          )}

          {/* CAMERA ERROR */}

          {cameraError && (
            <p
              style={{
                color: 'red',
              }}
            >
              ❌ {cameraError}
            </p>
          )}

          {/* START BUTTON */}

          <button
            onClick={startTracking}
            style={{
              padding:
                '12px 24px',

              marginTop: '15px',

              fontSize: '18px',

              border: 'none',

              borderRadius: '8px',

              cursor: 'pointer',
            }}
          >
            Start Tracking
          </button>
        </div>
      )}

      {/* ======================================
          SENSOR DATA
          ====================================== */}

      {cameraEnabled && (
        <div
          style={{
            position: 'absolute',

            top: '20px',
            left: '20px',

            padding: '15px',

            minWidth: '230px',

            borderRadius: '10px',

            backgroundColor:
              'rgba(0, 0, 0, 0.65)',

            color: 'white',

            fontFamily:
              'monospace',

            zIndex: 5,
          }}
        >
          <h3
            style={{
              marginTop: 0,
            }}
          >
            🟢 Tracking Active
          </h3>

          <p>
            <strong>
              Alpha:
            </strong>{' '}
            {orientation.alpha !== null
              ? `${orientation.alpha.toFixed(2)}°`
              : 'Waiting...'}
          </p>

          <p>
            <strong>
              Beta:
            </strong>{' '}
            {orientation.beta !== null
              ? `${orientation.beta.toFixed(2)}°`
              : 'Waiting...'}
          </p>

          <p>
            <strong>
              Gamma:
            </strong>{' '}
            {orientation.gamma !== null
              ? `${orientation.gamma.toFixed(2)}°`
              : 'Waiting...'}
          </p>

          <p>
            <strong>
              Absolute:
            </strong>{' '}
            {orientation.absolute
              ? 'Yes'
              : 'No'}
          </p>

          <p>
            <strong>
              Camera:
            </strong>{' '}
            ON
          </p>

          <button
            onClick={stopCamera}
            style={{
              padding:
                '8px 16px',

              border: '3px',

              borderRadius: '6px',

              cursor: 'pointer',
            }}
          >
            Stop Camera
          </button>
        </div>
      )}
    </main>
  );
}