# About

This is the Factual-supported Java driver for Factual's public API. It supports rich queries across Factual's U.S. Places, Crosswalk, and Crossref, and Resolve services.

Factual's [web-based API](http://developer.factual.com) offers:

* Places API: Rich queries across a high quality dataset of U.S. Points of Interest and business entities.
* Crosswalk: Translation between Factual IDs, third party IDs, and URLs that represent the same entity across the internet.
* Crossref: Lets you find URLs that contain entities in the Factual places database, or to find the Factual ID of a place mentioned on a URL.
* Resolve: An entity resolution API that makes partial records complete, matches one entity against another, and assists in de-duping and normalizing datasets.

# Installation

TODO: JAR_DOWNLOAD_LINK

# Basics

## Setup

	Factual factual = new Factual(MY_KEY, MY_SECRET);

## Constructing Read Queries

The <tt>Query</tt> object provides a fluent interface that allows you to add in constraints for things like limit, offset, full text searches, and row filters.

Once you've built up the Query you want, you call <tt>.read</tt> on your authorized Factual object, passing in the desired table name.

<tt>.read</tt> takes the table name as the first argument, followed by a <tt>Query</tt>. It returns a ReadResponse, which contains the results of the query.

## Simple Read Example

	;; Fetch 3 random records from Factual's "places" table and print their names
	ReadResponse resp = factual.read("places", new Query().limit(3));
	System.out.println(resp.mapStrings("name"));
	
### Full Text Search

	Query q = new Query();
	q.fullTextSearch("Sushi Santa Monica");

### Limit and Offset

You can use limit and offset to support basic results paging. For example:

	Query query = new Query().limit(10).offset(150);
	
### Geo Filters

	// Find records geographically located with 5000 meters of a latitude, longitude
	Query q = new Query();
	.within(new Circle(34.06018, -118.41835, 5000)));

### Row Filters

	// Find places whose name field starts with "Starbucks"
	Query query = new Query().field("name").startsWith("Starbucks");

	// Find places with a blank telephone number
	Query query = new Query().field("tel").isBlank();

#### Comparison operators

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

#### Search operator

To run full text searches that are constrained to specific fields (versus using .fullTextSearch on the full Query which searches all searchable fields in a table) you can:

	Query q = new Query();
	q.field("name").fullTextSearch("Fried Chicken");

#### AND

	Query q = new Query();
	q.and(
	  q.criteria("name").beginsWith("Coffee"),
	  q.criteria("tel").isBlank()
	);
    
Note that in this case, you could also simply do:

	Query q = new Query()
	.field("name").beginsWith("Coffee")
	.field("tel").isBlank();

This takes advantage of the fact that all row filters set at the top level of the Query are AND'ed together.

#### OR

	Query q = new Query();
	q.or(
	  q.criteria("tel").isBlank(),
	  q.criteria("name").startsWith("Starbucks"));
	  
#### Combined ANDs and ORs

	Query q = new Query();
	q.or(
	  q.or(
	    q.criteria("first_name").equal("Chun"),
	    q.criteria("last_name").equal("Kok")
	  ),
	  q.and(
	    q.criteria("score").equal("38"),
	    q.criteria("city").equal("Los Angeles")
	  )
	);

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

See the [detailed integration tests](https://github.com/Factual/factual-java-driver/blob/master/src/test/java/com/factual/FactualTest.java)
