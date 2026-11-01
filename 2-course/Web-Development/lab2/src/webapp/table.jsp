<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="results" scope="session" class="beans.ResultsBean"/>
<core:forEach var="result" items="${results.results}">
  <tr>
    <td>${result.x}</td>
    <td>${result.y}</td>
    <td>${result.r}</td>
    <td>${result.isHit ? "Hit" : "Didnt hit"}</td>
  </tr>
</core:forEach>
