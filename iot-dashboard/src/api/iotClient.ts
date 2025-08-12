// /src/api/iotClient.ts
// const BASE_URL = 'http://192.168.0.33:3000';
const BASE_URL = '';
const PATH = '/v1/rest/bedroom/lamp';

type LampState = 'on' | 'off';

function withTimeout(ms: number) {
  const controller = new AbortController();
  const id = setTimeout(() => controller.abort(), ms);
  return { controller, clear: () => clearTimeout(id) };
}

/**
 * Schaltet die Schlafzimmerlampe an/aus.
 * Entspricht z.B.: GET http://192.168.0.33:3000/v1/rest/bedroom/lamp/on
 */
export async function setBedroomLamp(state: LampState, timeoutMs = 5000): Promise<void> {
  const { controller, clear } = withTimeout(timeoutMs);
  try {
    const url = `${BASE_URL}${PATH}/${state}`;
    const res = await fetch(url, { method: 'POST', signal: controller.signal });
    if (!res.ok) {
      const body = await res.text().catch(() => '');
      throw new Error(`Lamp request failed (${res.status}): ${body || res.statusText}`);
    }
  } finally {
    clear();
  }
}

/**
 * Optional: ping zum schnellen Reachability‑Check.
 */
export async function pingBedroomLamp(timeoutMs = 3000): Promise<boolean> {
  const { controller, clear } = withTimeout(timeoutMs);
  try {
    const res = await fetch(`${BASE_URL}/health`, { signal: controller.signal }).catch(() => null);
    return !!res && res.ok;
  } finally {
    clear();
  }
}

// src/api/iotClient.ts
export interface RGB {
  r: number;
  g: number;
  b: number;
}

/**
 * Setzt die Schlafzimmer-LED auf eine RGB-Farbe.
 * Sendet POST /v1/rest/bedroom/led mit JSON-Body { r, g, b }
 */
export async function setBedroomLedColor(
  rgb: RGB,
  opts?: { timeoutMs?: number; baseUrl?: string }
): Promise<void> {
  const timeoutMs = opts?.timeoutMs ?? 5000;
  const baseUrl = '';
  const { controller, clear } = withTimeout(timeoutMs);

  try {
    const res = await fetch(`${baseUrl}/v1/rest/bedroom/led`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(rgb),
      cache: 'no-store',
      signal: controller.signal,
    });

    if (!res.ok) {
      const body = await res.text().catch(() => '');
      throw new Error(`LED color request failed (${res.status}): ${body || res.statusText}`);
    }
  } finally {
    clear();
  }
}
