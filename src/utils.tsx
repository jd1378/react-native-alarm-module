export function required(obj: unknown, key: string) {
  if (!obj) {
    throw new Error(
      "Object is undefined. cannot access '${key}' on undefined.",
    );
  }

  if (
    !(obj as Record<string, unknown>)[key] &&
    (obj as Record<string, unknown>)[key] !== false
  ) {
    throw new Error(`'${key}' is required.`);
  }
}
