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

## Simple Fetch

	;; Fetch 3 random Places from Factual and print their names
	ReadResponse resp = factual.read("places", new Query().limit(3));
	System.out.println(resp.mapStrings("name"));
        
<tt>read</tt> takes the table name as the first argument, followed by a Query. It returns a ReadResponse, which contains the results of the query.

## More Examples

See the [detailed integration tests](https://github.com/Factual/factual-java-driver/blob/master/src/test/java/com/factual/FactualTest.java)
