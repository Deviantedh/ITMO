<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form>
    <div class="input-group">
        <p>X:</p>
        <div id="x" class="button-group">
            <button type="button" onclick="changeX(-5)">-5</button>
            <button type="button" onclick="changeX(-4)">-4</button>
            <button type="button" onclick="changeX(-3)">-3</button>
            <button type="button" onclick="changeX(-2)">-2</button>
            <button type="button" onclick="changeX(-1)">-1</button>
            <button type="button" onclick="changeX(0)">0</button>
            <button type="button" onclick="changeX(1)">1</button>
            <button type="button" onclick="changeX(2)">2</button>
            <button type="button" onclick="changeX(3)">3</button>
        </div>
        <p>Selected X: <span id="selectedX">None</span></p>
    </div>
    <div class="input-group">
        <label for="y">Y:</label>
        <input type="text" id="y" name="y" maxlength="8" placeholder="Y: (-3, 3)">
    </div>
    <div class="input-group">
        <label for="r">R:</label>
        <div id="r" class="checkbox-group">
            <label><input type="checkbox" name="r" value="1">1</label>
            <label><input type="checkbox" name="r" value="1.5">1.5</label>
            <label><input type="checkbox" name="r" value="2">2</label>
            <label><input type="checkbox" name="r" value="2.5">2.5</label>
            <label><input type="checkbox" name="r" value="3">3</label>
        </div>
    </div>
    <script>
        function changeX(value) {
            document.getElementById('selectedX').innerText = value;
        }
    </script>
    <div class="check-btn">
        <input type="submit" value="Check">
    </div>
</form>
</body>
</html>
