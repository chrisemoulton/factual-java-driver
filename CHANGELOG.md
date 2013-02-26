## 1.7.3
 * Fix debugging print output.

## 1.7.2
 * Update resolve and match paths to use the latest API.

## 1.7.1
 * Deprecate .equal and add .isEqual for building query filters
 * Fix bug in setting offset/limit to 0
 
## 1.7.0
 * Flip diffs query methods so that before(…) indicates diffs are returned before the given time, and after indicates after the given time
 * Add support for streaming diffs requests for line-by-line processing

## 1.6.2

 * Updated demos and tests now expect auth in ~/.factual/factual-auth.yaml
 * Adds support for clear
 * Adds Date support for diffs
 * Adds read and connection timeout

## 1.6.1

 * Remove jackson-core-lgpl dependency in favor of jackson-core-asl
 * Add CPG tests
 * Deprecate getResponse() in FactualApiException in favor of getStatusCode() and getStatusMessage()

## 1.6.0

 * Make Factual class thread-safe.
 * Refactor Multi call: Remove queueFetch api. To use multi, create MultiRequest, add queries, and pass as arg to Factual class' sendRequests

## 1.5.3

 * Adds match feature.

## 1.5.2

 * Adds diffs feature. 
 * Updates Resolve for new behavior which returns either a single resolved result, or none at all.

## 1.5.1

 * Adds support for a raw get request using a path and url-encoded parameter string.
 * Fixes bug with list-based filters not being sent as an array in the query string. 

## 1.5.0

 * Removes deprecated Crosswalk-related classes.  Use a table read on the Crosswalk table instead.

## 1.4.3

 * Crosswalk updates: deprecate old API and document usage of Crosswalk table read.

## 1.4.2

 * Adds monetize API support.

## 1.4.1

 * Adds better testing, improved debugging, etc.

## 1.4.0

 * Adds Multi call
 * Adds geopulse
 * Adds reverse geocode

## 1.2.1

 * Renames Contribute feature to Submit

## 1.2.0

 * Adds support for facet, contribute, and flag features.
 * Adds raw read and debug info features.

## 1.1.0

 * Refactored into .driver package
 * Created a Tabular interface for responses that have tabulatable data
 * Fixed bug where lists ($in, etc.) were not formatted properly when sending JSON to API
 * Updated Google API Client Library dependency from 1.4.1-beta to 1.7.0-beta with relevant driver changes

## 1.0.2

 * Added support for fetching table schemas

## 1.0.1

 * Added more docs and demos

## 1.0

 * Initial release
