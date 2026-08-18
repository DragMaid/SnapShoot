'use client';

import { useEffect, useState } from 'react';

export interface DeviceOrientationState {
  alpha: number | null;
  beta: number | null;
  gamma: number | null;
  absolute: boolean;
}

// Safari/iOS exposes requestPermission()
// but TypeScript's standard definition may not include it.
type DeviceOrientationConstructor =
  typeof DeviceOrientationEvent & {
    requestPermission?: () => Promise<
      'granted' | 'denied'
    >;
  };

export function useDeviceOrientation() {
  const [orientation, setOrientation] =
    useState<DeviceOrientationState>({
      alpha: null,
      beta: null,
      gamma: null,
      absolute: false,
    });

  const [isSupported, setIsSupported] =
    useState<boolean>(false);

  const [isPermissionGranted, setIsPermissionGranted] =
    useState<boolean>(false);

  const [permissionError, setPermissionError] =
    useState<string | null>(null);

  // ==========================================
  // 1. CHECK DEVICE ORIENTATION SUPPORT
  // ==========================================

  useEffect(() => {
    if (typeof window === 'undefined') {
      return;
    }

    if (!('DeviceOrientationEvent' in window)) {
      setIsSupported(false);
      return;
    }

    setIsSupported(true);
  }, []);

  // ==========================================
  // 2. REQUEST DEVICE ORIENTATION PERMISSION
  // ==========================================

  async function requestPermission(): Promise<boolean> {
    if (typeof window === 'undefined') {
      return false;
    }

    setPermissionError(null);

    const OrientationEvent =
      window.DeviceOrientationEvent as unknown as DeviceOrientationConstructor;

    /*
     * iOS Safari:
     *
     * DeviceOrientationEvent.requestPermission()
     * exists here.
     */

    if (
      typeof OrientationEvent.requestPermission ===
      'function'
    ) {
      try {
        const permission =
          await OrientationEvent.requestPermission();

        console.log(
          'Device orientation permission:',
          permission
        );

        if (permission === 'granted') {
          setIsPermissionGranted(true);
          return true;
        }

        setIsPermissionGranted(false);

        setPermissionError(
          'Sensor permission was denied.'
        );

        return false;
      } catch (error) {
        console.error(
          'Sensor permission error:',
          error
        );

        setIsPermissionGranted(false);

        setPermissionError(
          'Unable to request sensor permission.'
        );

        return false;
      }
    }

    /*
     * Chrome Android / browsers that don't
     * expose requestPermission().
     *
     * There is no explicit permission function
     * to call here.
     */

    console.log(
      'Browser does not require explicit sensor permission.'
    );

    setIsPermissionGranted(true);

    return true;
  }

  // ==========================================
  // 3. LISTEN FOR DEVICE ORIENTATION
  // ==========================================

  useEffect(() => {
    if (!isPermissionGranted) {
      return;
    }

    function handleOrientation(
      event: DeviceOrientationEvent
    ) {
      console.log('Orientation:', {
        alpha: event.alpha,
        beta: event.beta,
        gamma: event.gamma,
        absolute: event.absolute,
      });

      setOrientation({
        alpha: event.alpha,
        beta: event.beta,
        gamma: event.gamma,
        absolute: event.absolute,
      });
    }

    window.addEventListener(
      'deviceorientation',
      handleOrientation
    );

    // Cleanup listener
    return () => {
      window.removeEventListener(
        'deviceorientation',
        handleOrientation
      );
    };
  }, [isPermissionGranted]);

  // ==========================================
  // 4. RETURN VALUES TO PAGE
  // ==========================================

  return {
    orientation,
    isSupported,
    isPermissionGranted,
    permissionError,
    requestPermission,
  };
}