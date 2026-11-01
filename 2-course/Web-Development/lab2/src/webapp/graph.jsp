<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="results" scope="session" class="beans.ResultsBean"/>
<html>
<head>
    <title>This is graph component</title>
</head>
<body>
<canvas id="graphCanvas" width="400" height="400"></canvas>
<script>
    let clickedPoints = [];

    function loadPoints() {
        clickedPoints = [
            <core:forEach var="result" items="${results.results}">
            {
                x: ${result.x},
                y: ${result.y},
                isHit: ${result.isHit ? 'true' : 'false'}
            },
            </core:forEach>
        ];
        // Remove the last comma to make it a valid array if needed
        if (clickedPoints.length > 0) {
            clickedPoints = clickedPoints.slice(0, -1);
        }
    }
</script>
<script type="module" src="main.js"></script>
</body>

</html>
