import inputValidator from "./inputValidator.js";

"use strict";

let message = "";
let clickedPoints = [];
let hasUserInputR;
let r;

function isPointInsideArea(x, y, R) {
    let validator = new inputValidator();
    validator.validate(x, y, R);
    message = validator.getMessage();
    return validator.getResponseCode() === 1;
}

function isRadiusAcceptable(R) {
    let validator = new inputValidator();
    validator.validateR(R);
    message = validator.getMessage();
    return validator.getResponseCode() === 1;
}

document.addEventListener("DOMContentLoaded", () => {
    function updateDateTime() {
        const now = new Date();
        document.getElementById("date").innerText = now.toDateString();
        document.getElementById("time").innerText = now.toTimeString().substring(0, 8);
    }

    updateDateTime();
    setInterval(updateDateTime, 1000);
});

window.onload = function () {
    setCanvasDPI();
    drawGraph(3);
}

const canvas = document.getElementById("graphCanvas");
const ctx = canvas.getContext("2d");

let dynamicScalingFactor;

function setCanvasDPI() {
    let dpi = window.devicePixelRatio;
    let canvasElement = document.getElementById('graphCanvas');
    let style = {
        height() {
            return +getComputedStyle(canvasElement).getPropertyValue('height').slice(0, -2);
        },
        width() {
            return +getComputedStyle(canvasElement).getPropertyValue('width').slice(0, -2);
        }
    };

    canvasElement.setAttribute('width', style.width() * dpi);
    canvasElement.setAttribute('height', style.height() * dpi);
}

function drawGraph(R) {
    let width = canvas.width;
    let height = canvas.height;

    let baseScaling = width / 6;
    dynamicScalingFactor = baseScaling / R;
    let yAxisOffset = 15;

    // Clear canvas
    ctx.clearRect(0, 0, width, height);
    ctx.font = "15px Arial";

// Draw x and y axes
    ctx.strokeStyle = "gray";
    ctx.lineWidth = 1;
    ctx.beginPath();
    drawAxis(ctx, 0, height / 2, width, height / 2);  // X-axis
    drawAxis(ctx, width / 2, 0, width / 2, height);   // Y-axis
    ctx.stroke();

// Dynamic scaling factor for the figure
//     const dynamicScalingFactor = width / 6 / R;

// Drawing the shapes

// Rectangle (upper right)
    ctx.fillStyle = "#0000FF20"; // blue with 20% opacity
    ctx.fillRect(width / 2, height / 2 - R * dynamicScalingFactor, R / 2 * dynamicScalingFactor, R * dynamicScalingFactor);
    ctx.strokeStyle = "#0000FF";
    ctx.strokeRect(width / 2, height / 2 - R * dynamicScalingFactor, R / 2 * dynamicScalingFactor, R * dynamicScalingFactor);

// Semi-circle (lower left)
//     ctx.fillStyle = "#39FF1420"; // green with 20% opacity
//     ctx.beginPath();
//     ctx.arc(width / 2, height / 2, R / 2 * dynamicScalingFactor, Math.PI, 1.5 * Math.PI);
//     ctx.lineTo(width / 2, height / 2);
//     ctx.closePath();
//     ctx.fill();
//     ctx.strokeStyle = "#39FF14";
//     ctx.beginPath();
//     ctx.arc(width / 2, height / 2, R / 2 * dynamicScalingFactor, Math.PI, 1.5 * Math.PI);
//     ctx.stroke();
    ctx.fillStyle = "#39FF1420"; // green with 20% opacity
    ctx.beginPath();
    ctx.arc(width / 2, height / 2, R / 2 * dynamicScalingFactor, 0.5*Math.PI, 1 * Math.PI);
    ctx.lineTo(width / 2, height / 2);
    ctx.closePath();
    ctx.fill();
    ctx.strokeStyle = "#39FF14";
    ctx.beginPath();
    ctx.arc(width / 2, height / 2, R / 2 * dynamicScalingFactor, 0.5 * Math.PI, 1 * Math.PI);
    ctx.stroke();


// Triangle (lower right)
    ctx.fillStyle = "#FFFF0020"; // yellow with 20% opacity
    ctx.beginPath();
    ctx.moveTo(width / 2, height / 2);
    ctx.lineTo(width / 2 + R / 2 * dynamicScalingFactor, height / 2);
    ctx.lineTo(width / 2, height / 2 + R * dynamicScalingFactor);
    ctx.closePath();
    ctx.fill();
    ctx.strokeStyle = "#FFFF00";
    ctx.stroke();

    // Draw labels
    ctx.fillStyle = "white";
    const labelR = hasUserInputR ? R.toString() : "R";
    const labelRHalf = hasUserInputR ? (R / 2).toString() : "R/2";

    // X-axis labels
    ctx.fillText(labelR, width / 2 + R * dynamicScalingFactor, height / 2 + 30);
    ctx.fillText(labelRHalf, width / 2 + (R / 2) * dynamicScalingFactor, height / 2 + 30);
    ctx.fillText('-' + labelR, width / 2 - R * dynamicScalingFactor, height / 2 + 30);
    ctx.fillText('-' + labelRHalf, width / 2 - (R / 2) * dynamicScalingFactor, height / 2 + 30);

    // Y-axis labels
    ctx.fillText(labelR, width / 2 + yAxisOffset, height / 2 - R * dynamicScalingFactor);
    ctx.fillText(labelRHalf, width / 2 + yAxisOffset, height / 2 - (R / 2) * dynamicScalingFactor);
    ctx.fillText('-' + labelR, width / 2 + yAxisOffset, height / 2 + R * dynamicScalingFactor);
    ctx.fillText('-' + labelRHalf, width / 2 + yAxisOffset, height / 2 + (R / 2) * dynamicScalingFactor);

    // Draw ticks
    ctx.fillStyle = "white";
    // X-axis tics
    const tickLength = 10; // Length of the tick marks
    for (let tickValue = -R; tickValue <= R; tickValue += R / 2) {
        const xTickPosition = width / 2 + tickValue * dynamicScalingFactor;
        ctx.beginPath();
        ctx.moveTo(xTickPosition, height / 2 - tickLength / 2);
        ctx.lineTo(xTickPosition, height / 2 + tickLength / 2);
        ctx.stroke();
    }

    // Y-axis tics
    for (let tickValue = -R; tickValue <= R; tickValue += R / 2) {
        const yTickPosition = height / 2 - tickValue * dynamicScalingFactor;
        ctx.beginPath();
        ctx.moveTo(width / 2 - tickLength / 2, yTickPosition);
        ctx.lineTo(width / 2 + tickLength / 2, yTickPosition);
        ctx.stroke();
    }
    if (hasUserInputR) {
        // console.log("drawing points");
        drawAllPoints();
    }
}

function drawAxis(context, fromX, fromY, toX, toY) {
    const headLength = 10;
    const angle = Math.atan2(toY - fromY, toX - fromX);

    context.beginPath();
    context.moveTo(fromX, fromY);
    context.lineTo(toX, toY);
    context.lineTo(toX - headLength * Math.cos(angle - Math.PI / 6), toY - headLength * Math.sin(angle - Math.PI / 6));
    context.moveTo(toX, toY);
    context.lineTo(toX - headLength * Math.cos(angle + Math.PI / 6), toY - headLength * Math.sin(angle + Math.PI / 6));
    context.stroke();
}

function drawPoint(x, y, isHit) {
    let canvasX = canvas.width / 2 + x * dynamicScalingFactor;
    let canvasY = canvas.height / 2 - y * dynamicScalingFactor;

    ctx.fillStyle = isHit ? "green" : "red" ;
    ctx.beginPath();
    ctx.arc(canvasX, canvasY, 5, 0, Math.PI * 2);
    ctx.fill();
}

async function drawAllPoints() {
    for (const point of clickedPoints) {
        const isInside = await isPointInsideDesiredArea(point.x, point.y, r);
        drawPoint(point.x, point.y, isInside);
    }
}

function updateTableAndGraph(data) {

    let parsedX = parseFloat(data.x);
    let parsedY = parseFloat(data.y);
    let parsedR = parseFloat(data.r);

    // Draw the new point directly
    drawPoint(parsedX, parsedY, data.isHit);

    // Push it to clickedPoints
    clickedPoints.push({x: parsedX, y: parsedY, r: parsedR, isHit: data.isHit});

    // Update Table
    // const table = document.getElementById('result');
    // const newRow = table.insertRow();
    // const cell1 = newRow.insertCell(0);
    // const cell2 = newRow.insertCell(1);
    // const cell3 = newRow.insertCell(2);
    // const cell4 = newRow.insertCell(3);
    //
    // cell1.innerHTML = data.x;
    // cell2.innerHTML = data.y;
    // cell3.innerHTML = data.r;
    // cell4.innerHTML = data.isHit ? 'Hit' : 'Didn\'t hit';
}

const mainForm = document.querySelector('input[value="Check"]');
mainForm.addEventListener('click', function (e) {
    // default action is to send the form data to the server and reload the page
    // by calling .preventDefault() I am stopping the browser from doing this,
    // which allows me to handle the form submission programmatically in your JavaScript code instead.
    e.preventDefault();

    const xElement = document.getElementById('selectedX');
    const yElement = document.querySelector('#y');
    const rElement = document.querySelector('input[name="r"]:checked');

    if (xElement && yElement && rElement) {
        const xVal = parseFloat(xElement.innerText);
        const yVal = parseFloat(yElement.value);
        const rVal = parseFloat(rElement.value);
        console.log(`X: ${xVal}, Y: ${yVal}, R: ${rVal}`);
        if (isPointInsideArea(xVal, yVal, rVal)) {
            console.log(`client validation: passed`);

            fetch("controller", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: new URLSearchParams({
                    "x": xVal,
                    "y": yVal,
                    "r": rVal
                })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Server responded with bad getaway status: ${response.status} ${response.text()}`);
                    }
                    return response.json();
                })
                .then(function (serverAnswer) {
                    updateTableAndGraph(serverAnswer);
                })
                .catch(error => {
                    showToast(`There was an error processing your request: ${error.message}`);
                });
        } else {
            console.log(`client validation: failed`);
            showToast(message);
        }
    } else {
        showToast("You should fill the form before submitting it :)");
    }
});
function getSelectedR() {
    const rElements = document.querySelectorAll('input[name="r"]:checked');
    if (rElements.length === 0) {
        showToast("Пожалуйста, выберите радиус R.", "error");
        return null;
    }
    if (rElements.length > 1) {
        showToast("Выберите только одно значение радиуса R.", "error");
        return null;
    }
    return parseFloat(rElements[0].value);
}

// canvas.addEventListener("click", function (event) {
//     const rElement = document.querySelector('#r');
//     if (!rElement || !rElement.value || isNaN(parseFloat(rElement.value))) {
//         showToast("Невозможно определить координаты точки без радиуса");
//         return;
//     }
//
//     let x = event.clientX - canvas.getBoundingClientRect().left;
//     let y = event.clientY - canvas.getBoundingClientRect().top;
//     let R = parseFloat(rElement.value);
//
//     let graphX = (x - canvas.width / 2) / dynamicScalingFactor;
//     let graphY = (canvas.height / 2 - y) / dynamicScalingFactor;
//
//     // Round and constrain graphX to the set [-4, -3, -2, -1, 0, 1, 2, 3, 4]
//     graphX = Math.round(graphX);
//
//     console.log(`Raw values: X: ${x}, Y: ${y}, R: ${R}`);
//     console.log(`Graph values: ${graphX}, ${graphY}`);
//
//     if (isPointInsideArea(graphX, graphY, R)) {
//
//         fetch("controller", {
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/x-www-form-urlencoded",
//             },
//             body: new URLSearchParams({
//                 "x": graphX,
//                 "y": graphY,
//                 "r": R
//             })
//         })
//             .then(response => {
//                 if (!response.ok) {
//                     throw new Error(`Server responded with bad getaway status: ${response.status} ${response.text()}`);
//                 }
//                 return response.json();
//             })
//             .then(function (serverAnswer) {
//                 updateTableAndGraph(serverAnswer);
//             })
//             .catch(error => {
//                 showToast(`There was an error processing your request: ${error.message}`);
//             });
//     } else {
//         console.log(`client validation: failed`);
//         showToast(message);
//     }
// });
canvas.addEventListener("click", function (event) {
    const R = getSelectedR(); // Получаем выбранное значение R
    if (R === null) {
        return; // Если R не выбран или выбрано несколько, прекращаем выполнение
    }

    let x = event.clientX - canvas.getBoundingClientRect().left;
    let y = event.clientY - canvas.getBoundingClientRect().top;

    let graphX = (x - canvas.width / 2) / dynamicScalingFactor;
    let graphY = (canvas.height / 2 - y) / dynamicScalingFactor;


    //graphX = Math.round(graphX);
    graphX = parseFloat(graphX.toFixed(2));
    graphY = parseFloat(graphY.toFixed(2));
    console.log(`Raw values: X: ${x}, Y: ${y}, R: ${R}`);
    console.log(`Graph values: ${graphX}, ${graphY}`);

    if (isPointInsideArea(graphX, graphY, R)) {
        fetch("controller", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: new URLSearchParams({
                "x": graphX,
                "y": graphY,
                "r": R
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server responded with bad gateway status: ${response.status} ${response.statusText}`);
                }
                return response.json();
            })
            .then(function (serverAnswer) {
                updateTableAndGraph(serverAnswer);
            })
            .catch(error => {
                showToast(`Ошибка при обработке запроса: ${error.message}`, "error");
            });
    } else {
        console.log(`client validation: failed`);
        showToast(message, "error");
    }
});


// const rElement = document.querySelector('#r');
// rElement.addEventListener('input', function () {
//     r = parseFloat(rElement.value);
//     if (isNaN(r)) {
//         r = 3; // Default R value
//         hasUserInputR = false;
//     } else {
//         hasUserInputR = true;
//     }
//     if (isRadiusAcceptable(r)) {
//         console.log(`Drawing graph with r: ${r}`);
//         drawGraph(r);  // Drawing the graph based on new R value
//     } else {
//         showToast(message);
//     }
// });
const rElement = document.querySelector('#r');
rElement.addEventListener('input', function () {
    const selectedR = getSelectedR(); // Проверяем выбранное значение R
    if (selectedR === null) {
        return; // Если R не выбран или выбрано несколько, не обновляем график
    }
    r = selectedR;
    hasUserInputR = true;

    if (isRadiusAcceptable(r)) {
        console.log(`Drawing graph with R: ${r}`);
        drawGraph(r); // Обновляем график
    } else {
        showToast(message, "error");
    }
});


async function isPointInsideDesiredArea(x, y, r) {
    try {
        const response = await fetch("controller", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: new URLSearchParams({
                "x": x,
                "y": y,
                "r": r
            })
        })

        if (!response.ok) {
            throw new Error(`Server responded with bad getaway status: ${response.status} ${await response.text()}`);
        }

        const serverAnswer = await response.json();
        return serverAnswer.isHit;
    } catch (error) {
        showToast(`There was an error processing your request: ${error.message}`);
        return null;
    }
}

function showToast(message, className = "info", offsetX = -300, offsetY = 140, position = "right") {
    Toastify({
        text: message,
        className: className,
        style: {
            background: "linear-gradient(to right, #00b09b, #96c93d)",
            border: "1px solid white"
        },
        offset: {
            x: offsetX,
            y: offsetY
        },
        position: position
    }).showToast();
}
