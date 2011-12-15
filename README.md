# About

This is the Factual-supported Java driver for [Factual's public API](http://developer.factual.com).

# Installation

The driver is in Maven Central, so you can just add this to your Maven <tt>pom.xml</tt>:

    <dependency>
      <groupId>com.factual</groupId>
      <artifactId>factual-java-driver</artifactId>
      <version>1.0</version>
    </dependency>

# Basic Design

The driver allows you to create an authenticated handle to Factual. With a Factual handle, you can send queries and get results back.

Queries are created using the Query class, which provides a fluent interface to constructing your queries.

Results are returned as the JSON returned by Factual. Optionally, there are JSON parsing conveniences built into the driver.

# Setup

    // Create an authenticated handle to Factual
    Factual factual = new Factual(MY_KEY, MY_SECRET);
    
# Simple Query Example

    // Print 3 random records from Factual's Places table:
    System.out.println(
      factual.fetch("places", new Query().limit(3)));
	
# Full Text Search

    // Print entities that match a full text search for Sushi in Santa Monica:
    System.out.println(
        factual.fetch("places", new Query().search("Sushi Santa Monica")));

# Geo Filters

You can query Factual for entities located within a geographic area. For example:

    // Build a Query that finds entities located within 5000 meters of a latitude, longitude
    new Query().within(new Circle(34.06018, -118.41835, 5000));

# Results sorting

You can have Factual sort your query results for you, on a field by field basis. Simple example:

    // Build a Query to find 10 random entities and sort them by name, ascending:
    new Query().limit(10).sortAsc("name");
    
You can specify more than one sort, and the results will be sorted with the first sort as primary, the second sort or secondary, and so on:

    // Build a Query to find 20 random entities, sorted ascending primarily by region, then by locality, then by name:
    q = new Query()
      .limit(20)
      .sortAsc("region")
      .sortAsc("locality")
      .sortDesc("name");

# Limit and Offset

You can use limit and offset to support basic results paging. For example:

    // Build a Query with offset of 150, limiting the page size to 10:
    new Query().limit(10).offset(150);
	
# Field Selection

By default your queries will return all fields in the table. You can use the only modifier to specify the exact set of fields returned. For example:

    // Build a Query that only gets the name, tel, and category fields:
    new Query().only("name", "tel", "category");
    
# All Top Level Query Parameters

<table>
  <tr>
    <th>Parameter</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>filters</td>
    <td>Restrict the data returned to conform to specific conditions.</td>
    <td>q.field("name").beginsWith("Starbucks")</td>
  </tr>
  <tr>
    <td>include count</td>
    <td>Include a count of the total number of rows in the dataset that conform to the request based on included filters. Requesting the row count will increase the time required to return a response. The default behavior is to NOT include a row count. When the row count is requested, the Response object will contain a valid total row count via <tt>.getTotalRowCount()</tt>.</td>
    <td><tt>q.includeRowCount()</tt></td>
  </tr>
  <tr>
    <td>geo</td>
    <td>Restrict data to be returned to be within a geographical range based.</td>
    <td>(See the section on Geo Filters)</td>
  </tr>
  <tr>
    <td>limit</td>
    <td>Maximum number of rows to return. Default is 20. The system maximum is 50. For higher limits please contact Factual, however consider requesting a download of the data if your use case is requesting more data in a single query than is required to fulfill a single end-user's request.</td>
    <td><tt>q.limit(10)</tt></td>
  </tr>
  <tr>
    <td>search</td>
    <td>Full text search query string.</td>
    <td>
      Find "sushi":<br><tt>q.search("sushi")</tt><p>
      Find "sushi" or "sashimi":<br><tt>q.search("sushi, sashimi")</tt><p>
      Find "sushi" and "santa" and "monica":<br><tt>q.search("sushi santa monica")</tt>
    </td>
  </tr>
  <tr>
    <td>offset</td>
    <td>Number of rows to skip before returning a page of data. Maximum value is 500 minus any value provided under limit. Default is 0.</td>
    <td><tt>q.offset(150)</tt></td>
  </tr>
  <tr>
    <td>only</td>
    <td>What fields to include in the query results.  Note that the order of fields will not necessarily be preserved in the resulting JSON response due to the nature of JSON hashes.</td>
    <td><tt>q.only("name", "tel", "category")</tt></td>
  </tr>
  <tr>
    <td>sort</td>
    <td>The field (or secondary fields) to sort data on, as well as the direction of sort.  Supports $distance as a sort option if a geo-filter is specified.  Supports $relevance as a sort option if a full text search is specified either using the q parameter or using the $search operator in the filter parameter.  By default, any query with a full text search will be sorted by relevance.  Any query with a geo filter will be sorted by distance from the reference point.  If both a geo filter and full text search are present, the default will be relevance followed by distance.</td>
    <td><tt>q.sortAsc("name").sortDesc("$distance")</tt></td>
  </tr>
</table>  

# Row Filters

The driver supports various row filter logic. Examples:

    // Build a query to find places whose name field starts with "Starbucks"
    new Query().field("name").beginsWith("Starbucks");

    // Build a query to find places with a blank telephone number
    new Query().field("tel").blank();

## Supported row filter logic

<table>
  <tr>
    <th>Predicate</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>equal</td>
    <td>equal to</td>
    <td><tt>q.field("region").equal("CA")</tt></td>
  </tr>
  <tr>
    <td>notEqual</td>
    <td>not equal to</td>
    <td><tt>q.field("region").notEqual("CA")</tt></td>
  </tr>
  <tr>
    <td>search</td>
    <td>full text search</td>
    <td><tt>q.field("name").search("fried chicken")</tt></td>
  </tr>
  <tr>
    <td>in</td>
    <td>equals any of</td>
    <td><tt>q.field("region").in("MA", "VT", "NH", "RI", "CT")</tt></td>
  </tr>
  <tr>
    <td>notIn</td>
    <td>does not equal any of</td>
    <td><tt>q.field("locality").notIn("Los Angeles")</tt></td>
  </tr>
  <tr>
    <td>beginsWith</td>
    <td>begins with</td>
    <td><tt>q.field("name").beginsWith("b")</tt></td>
  </tr>
  <tr>
    <td>notBeginsWith</td>
    <td>does not begin with</td>
    <td><tt>q.field("name").notBeginsWith("star")</tt></td>
  </tr>
  <tr>
    <td>beginsWithAny</td>
    <td>begins with any of</td>
    <td><tt>q.field("name").beginsWithAny("star", "coffee", "tull")</tt></td>
  </tr>
  <tr>
    <td>notBeginsWithAny</td>
    <td>does not begin with any of</td>
    <td><tt>q.field("name").notBeginsWithAny("star", "coffee", "tull")</tt></td>
  </tr>
  <tr>
    <td>blank</td>
    <td>is blank or null</td>
    <td><tt>q.field("tel").blank()</tt></td>
  </tr>
  <tr>
    <td>notBlank</td>
    <td>is not blank or null</td>
    <td><tt>q.field("tel").notBlank()</tt></td>
  </tr>
  <tr>
    <td>greaterThan</td>
    <td>greater than</td>
    <td><tt>q.field("rating").greaterThan(7.5)</tt></td>
  </tr>
  <tr>
    <td>greaterThanOrEqual</td>
    <td>greater than or equal to</td>
    <td><tt>q.field("rating").greaterThanOrEqual(7.5)</tt></td>
  </tr>
  <tr>
    <td>lessThan</td>
    <td>less than</td>
    <td><tt>q.field("rating").lessThan(7.5)</tt></td>
  </tr>
  <tr>
    <td>lessThanOrEqual</td>
    <td>less than or equal to</td>
    <td><tt>q.field("rating").lessThanOrEqual(7.5)</tt></td>
  </tr>
</table>

## AND

Queries support logical AND'ing your row filters. For example:

    // Build a query to find entities where the name begins with "Coffee" AND the telephone is blank:
    Query q = new Query();
    q.and(
      q.criteria("name").beginsWith("Coffee"),
      q.criteria("tel").blank()
    );
    
Note that all row filters set at the top level of the Query are implicitly AND'ed together, so you could also do this:

    new Query()
      .field("name").beginsWith("Coffee")
      .field("tel").blank();

## OR

Queries support logical OR'ing your row filters. For example:

    // Build a query to find entities where the name begins with "Coffee" OR the telephone is blank:
    Query q = new Query();
    q.or(
        q.criteria("name").beginsWith("Coffee"),
        q.criteria("tel").blank());
	  
## Combined ANDs and ORs

You can nest AND and OR logic to whatever level of complexity you need. For example:

    // Build a query to find entities where:
    // (name begins with "Starbucks") OR (name begins with "Coffee")
    // OR
    // (name full text search matches on "tea" AND tel is not blank)
    Query q = new Query();
    q.or(
        q.or(
            q.criteria("name").beginsWith("Starbucks"),
            q.criteria("name").beginsWith("Coffee")
        ),
        q.and(
            q.criteria("name").search("tea"),
            q.criteria("tel").notBlank()
        )
    );

# Crosswalk

The driver fully support Factual's Crosswalk feature, which lets you "crosswalk" the web and relate entities between Factual's data and that of other web authorities.

(See [the Crosswalk Blog](http://blog.factual.com/crosswalk-api) for more background.)

## Simple Crosswalk Example

    // Get all Crosswalk data for a specific Places entity, using its Factual ID:
    factual.fetch("places",
      new CrosswalkQuery().factualId("97598010-433f-4946-8fd5-4a6dd1639d77"));

## Crosswalk Filter Parameters

<table>
  <tr>
    <th>Filter</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>factualId</td>
    <td>A Factual ID for an entity in the Factual places database</td>
    <td><tt>q.factualId("97598010-433f-4946-8fd5-4a6dd1639d77")</tt></td>
  </tr>
  <tr>
    <td>limit</td>
    <td>A Factual ID for an entity in the Factual places database</td>
    <td><tt>q.limit(100)</tt></td>
  </tr>
  <tr>
    <td>namespace</td>
    <td>The namespace to search for a third party ID within. A list of <b>currently supported</b> services is <a href="http://developer.factual.com/display/docs/Places+API+-+Supported+Crosswalk+Services">here</a>.</td>
    <td><tt>q.namespace("foursquare")</tt></td>
  </tr>
  <tr>
    <td>namespaceId</td>
    <td>The id used by a third party to identify a place.</td>
    <td><tt>q.namespaceId("443338")</tt></td>
  </tr>
  <tr>
    <td>only</td>
    <td>A Factual ID for an entity in the Factual places database</td>
    <td><tt>q.only("foursquare", "yelp")</tt></td>
  </tr>
</table>

NOTE: although these parameters are individually optional, at least one of the following parameter combinations is required:

* factualId
* namespace and namespaceId

## More Crosswalk Examples

    // Get Loopt's Crosswalk data for a specific Places entity, using its Factual ID:
    CrosswalkResponse resp = factual.fetch("places",
        new CrosswalkQuery()
          .factualId("97598010-433f-4946-8fd5-4a6dd1639d77")
          .only("loopt"));

    // Get all Crosswalk data for a specific Places entity, using its Foursquare ID
    CrosswalkResponse resp = factual.fetch("places",
        new CrosswalkQuery()
          .namespace("foursquare")
          .namespaceId("4ae4df6df964a520019f21e3"));
          
# Exception Handling

If Factual's API indicates an error, a <tt>FactualApiException</tt> unchecked Exception will be thrown. It will contain details about the request you sent and the error that Factual returned.

Here is an example of catching a <tt>FactualApiException</tt> and inspecting it:

    Factual badness = new Factual("BAD_KEY", "BAD_SECRET");
    try{
      badness.read("places", new Query().field("country").equal(true));
    } catch (FactualApiException e) {
      System.out.println("Requested URL: " + e.getRequestUrl());
      System.out.println("Error Status Code: " + e.getResponse().statusCode);
      System.out.println("Error Response Message: " + e.getResponse().statusMessage);
    }

# More Examples

For more code examples:

* See the standalone demos in <tt>src/test/java/com/factual/demo</tt>
* See the integration tests in <tt>src/test/java/com/factual/FactualTest.java</tt>
