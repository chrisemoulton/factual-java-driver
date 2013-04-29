## 1.7.7-android
 * Add resolve debug to see all candidates
 * Add shape query
 * Add method to get rows near a point

## 1.7.6-android
 * Update for latest geopulse API changes
 
## 1.7.5-android
 * Add row queries
 * Fix debugging print output.
 * Add "includes" and "includes_any" filters

## 1.7.2-android
 * Update resolve and match paths to use the latest API.
 * Deprecate .equal and add .isEqual for building query filters
 * Fix bug in setting offset/limit to 0
 
## 1.7.0-android
 * Flip diffs query methods so that before(…) indicates diffs are returned before the given time, and after indicates after the given time
 * Add support for streaming diffs requests for line-by-line processing

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
