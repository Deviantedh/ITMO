<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>500 error page</title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/styles/main.css"/>
</head>
<body>
<div class="w-full h-screen flex flex-col items-center justify-center">
    <svg class="w-1/2 md:w-1/3 lg:w-1/4 text-blue-600 mx-auto" xmlns="http://www.w3.org/2000/svg" data-name="Layer 1"
         viewBox="0 0 593 593" xmlns:xlink="http://www.w3.org/1999/xlink">
        <g transform="translate(0.000000,512.000000) scale(0.100000,-0.100000)"
           fill="#FF3C26" stroke="none">
            <path d="M2405 4794 c-552 -45 -1052 -276 -1435 -663 -376 -380 -593 -854
-641 -1396 -50 -584 120 -1126 507 -1608 326 -407 824 -692 1373 -784 150 -25
552 -25 702 0 375 63 723 213 1022 440 76 58 279 252 321 306 185 241 235 319
321 496 207 429 268 884 185 1369 -118 688 -580 1300 -1215 1612 -216 106
-433 174 -669 209 -103 15 -380 26 -471 19z m585 -1301 l20 -26 -2 -936 c-3
-1047 3 -957 -77 -1059 -29 -36 -59 -60 -108 -84 -58 -28 -77 -33 -143 -33
-129 1 -238 68 -295 182 -27 53 -30 69 -33 185 l-4 127 -446 3 c-442 3 -448 3
-493 26 -61 30 -121 89 -150 149 -32 66 -32 178 0 245 40 82 143 167 204 168
16 0 17 27 17 424 0 490 1 498 80 577 80 80 52 77 772 78 l637 1 21 -27z m684
6 l26 -20 0 -792 0 -791 -22 -23 c-21 -23 -24 -23 -249 -23 l-228 0 -20 26
c-21 26 -21 33 -21 811 l0 784 25 24 24 25 219 0 c207 0 221 -1 246 -21z"/>
        </g>
    </svg>

    <div class="flex flex-col items-center justify-center">
        <p class="text-5xl md:text-6xl lg:text-7xl font-bold tracking-wider text-gray-600 mt-8">500</p>
        <p class="text-2xl md:text-3xl lg:text-4xl font-bold text-gray-600 mt-2">Server Error</p>
        <p class="md:text-lg xl:text-xl text-gray-500 mt-4">Всё поломалось.</p>
    </div>
</div>
</body>
</html>
