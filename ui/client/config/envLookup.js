/**
 * The set of environment variables used by spog-ui plus "NODE_ENV".  This list
 * needs to be kept in sync with `client/src/app/env.ts`.
 */
const SPOG_ENV = ["NODE_ENV", "PROFILE", "VERSION"];

/**
 * Return an object containing the environment variables listed in `TACKLE_ENV` and
 * their values.
 */
const getEnv = (vars = SPOG_ENV) =>
  Object.fromEntries(vars.map((key) => [key, process.env[key]]));

/**
 * Return a base64 encoded JSON string containing the environment variables listed in
 * `SPOG_ENV` and their values.
 */
export const getEncodedEnv = () =>
  Buffer.from(JSON.stringify(getEnv())).toString("base64");
