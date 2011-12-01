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

## Simple Read

	;; Fetch 3 random Places from Factual and print their names
	ReadResponse resp = factual.read("places", new Query().limit(3));
	System.out.println(resp.mapStrings("name"));
        
<tt>read</tt> takes the table name as the first argument, followed by a <tt>Query</tt>. It returns a ReadResponse, which contains the results of the query.

## Constructing Read Queries

The <tt>Query</tt> object provides a fluent interface that allows you to add in constraints for things like limit, offset, full text searches, and row filters.

### row filters

	// Find places whose name field starts with "Starbucks"
	Query query = new Query().field("name").startsWith("Starbucks")

	// Find places with a blank telephone number
	Query query = new Query().field("tel").isBlank();

#### suported predicates

<table>
  <tr>
    <th>Predicate</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td>equal</td>
    <td>equal to</td>
    <td>q.field("region").equal("CA")</td>
  </tr>
  <tr>
    <td>notEqual</td>
    <td>not equal to</td>
    <td>q.field("region").notEqual("CA")</td>
  </tr>
  <tr>
    <td>in</td>
    <td>equals any of</td>
    <td>q.field("region").in("MA", "VT", "NH", "RI", "CT")</td>
  </tr>
</table>    

#### AND

#### OR

	Query q = new Query();
	q.or(
	  q.criteria("tel").isBlank(),
	  q.criteria("name").startsWith("Starbucks"));
	  
#### Combined ANDs and ORs

### limit and offset

You can use limit and offset to support basic results paging. For example:
	Query query = new Query().limit(10).offset(150);

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
