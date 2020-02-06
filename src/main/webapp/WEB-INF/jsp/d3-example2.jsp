<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html lang="en">
<head xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:spring="http://www.springframework.org/tags">
<meta charset="utf-8" />
<script type="text/javascript" src="/d3.js"></script>
<title>A Pagey Page2</title>
</head>
<body>
    <h1>Hello world!3</h1>
    <p>What up?</p>
    <script type="text/javascript">
        d3.select("body").append("p").text("D3 works!");
    </script>
</body>
</html>