## 1.6.2-android

 * Updated demos and tests now expect auth in ~/.factual/factual-auth.yaml
 * Adds support for clear
 * Adds Date support for diffs
 * Adds read and connection timeout
 * Remove jackson-core-lgpl dependency in favor of jackson-core-asl
 * Add CPG tests
 * Deprecate getResponse() in FactualApiException in favor of getStatusCode() and getStatusMessage()
 * Make Factual class thread-safe.
 * Refactor Multi call: Remove queueFetch api. To use multi, create MultiRequest, add queries, and pass as arg to Factual class' sendRequests
  
## 1.5.3-android
 * Adds match feature.

## 1.5.2-android
 * Adds diffs feature. 
 * Updates Resolve for new behavior which returns either a single resolved result, or none at all.

## 1.5.1-android
 * Adds support for a raw get request using a path and url-encoded parameter string.
 * Fixes bug with list-based filters not being sent as an array in the query string. 

## 1.5.0-android
 * Removes deprecated Crosswalk-related classes.  Use a table read on the Crosswalk table instead.

## 1.4.3-android
 * Crosswalk updates: deprecate old API and document usage of Crosswalk table read.
 * Adds monetize API support.
 * Adds better testing, improved debugging, etc.

## 1.4.1-android
 * Adds Multi call
 * Adds geopulse
 * Adds reverse geocode

## 1.2.1-android

 * Initial release
