import inputValidator from "./inputValidator.js";

"use strict";

document.addEventListener("DOMContentLoaded", () => {
    // Update Date and Time Function
    function updateDateTime() {
        const now = new Date();
        document.getElementById("date").innerText = now.toDateString();
        document.getElementById("time").innerText = now.toTimeString().substring(0, 8);
    }

    // Initial Date Time Update
    updateDateTime();
    setInterval(updateDateTime, 1000);
});

window.onload = function () {
    //const savedData = JSON.parse(localStorage.getItem('tableData')) || [];    Хранение данных в js
    //const savedData = JSON.parse(localStorage.getItem('session')) || [];
    // savedData.forEach(data => {
    //     addToTable(data.x, data.y, data.r, data.result, data.curr_time, data.exec_time);
    // })

    const tableBody = document.getElementById("output");
    tableBody.innerHTML = '';
/*
    savedData.forEach(data => {
        const newRow = document.createElement("tr");
        // Создаём ячейки и вставляем данные
        newRow.innerHTML = `
                    <td>${data.x || 'undefined'}</td>
                    <td>${data.y || 'undefined'}</td>
                    <td>${data.r || 'undefined'}</td>
                    <td>${data.coordsStatus || 'undefined'}</td>
                    <td>${data.currentTime || 'undefined'}</td>
                    <td>${data.benchmarkTime || 'undefined'}</td>
                    `;

        // Добавляем новую строку в таблицу
        tableBody.appendChild(newRow);
    });

 */
    //console.log(localStorage.getItem("session"));
    //document.getElementById("output").innerHTML = localStorage.getItem("session");

}

const mainForm = document.querySelector('input[value="Check"]');
mainForm.addEventListener('click', async function (e) {
    e.preventDefault();

    const xElement = document.querySelector('input[name="x"]:checked');
    const yElement = document.querySelector('#y');
    const rElement = document.querySelector('#r');
    // const response = await fetch('/fcgi-bin/fastCGI-1.0-SNAPSHOT-jar-with-dependencies.jar', {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json'
    //     },
    //     body: JSON.stringify(payload)
    // });
    if (xElement && yElement && rElement) {
        const xVal = parseFloat(xElement.value);
        const yVal = parseFloat(yElement.value.substring(0, 8));
        const rVal = parseFloat(rElement.value);
        console.log(`X: ${xVal}, Y: ${yVal}, R: ${rVal}`);

        let validator = new inputValidator();
        validator.validate(xVal, yVal, rVal);

        if (validator.getResponseCode() === 1) {
            console.log(`everything is ok`);
            const params = new URLSearchParams({
                x: xVal,
                y: yVal,
                r: rVal
            });

            //${params.toString()}
            /*fetch(`/fcgi-bin/web1.jar?xVal=${xVal}&yVal=${yVal}&rVal=${rVal}`, {method: "POST"
        })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Server responded with bad getaway status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(function (serverAnswer) {
                    const responseData = JSON.parse(serverAnswer);
                    console.log(`${responseData}`)
                    localStorage.setItem("session", responseData);
                    document.getElementById("output").innerHTML = responseData;
                    console.log(`${responseData}`)
                    //localStorage.setItem("session", JSON.stringify(serverAnswer));
                    //document.getElementById("output").innerHTML = JSON.stringify(serverAnswer);
                })
                .catch(error => {
                    alert(`There was an error processing your request: ${error.message}`)
                })

             */
            const requestContent = {
                method: "POST",
                headers: {
                    "content-type": "application/json",
                },
                body: JSON.stringify({
                    xVal: xVal,
                    yVal: yVal,
                    rVal: rVal
                })
            };

            /*
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    xVal: xVal,
                    yVal: yVal,
                    rVal: rVal
                }
             */
            fetch(`/fcgi-bin/web1.jar?xVal=${xVal}&yVal=${yVal}&rVal=${rVal}`, requestContent
            //     {
            //     method: "POST"
            // }
            )



            // fetch(`/fcgi-bin/web1.jar`, requestContent
            // )
                .then(response => {
                    // Логируем статус ответа
                    console.log(JSON.stringify({
                        xVal: xVal,
                        yVal: yVal,
                        rVal: rVal
                    }))
                    console.log(`Response status: ${response.status}`);

                    if (!response.ok) {
                        throw new Error(`Server responded with bad getaway status: ${response.status}`);
                    }

                    // Логируем сырые данные ответа перед парсингом
                    return response.text().then(text => {
                        console.log(`Raw response text: ${text}`);
                        try {
                            const jsonData = JSON.parse(text);
                            return jsonData; // Возвращаем данные для следующего then
                        } catch (error) {
                            throw new Error("Failed to parse JSON: " + error.message);
                        }
                    });
                })
                .then(function (serverAnswer) {
                    // Логируем ответ, который был успешно спарсен
                    console.log('Parsed JSON response:', serverAnswer);

                    // Сохраняем данные в localStorage
                    localStorage.setItem("session", JSON.stringify(serverAnswer));

                    const tableBody = document.getElementById("output");
                    tableBody.innerHTML = '';

                    serverAnswer.forEach(data => {
                    const newRow = document.createElement("tr");
                    /*
                    переделать пост через боди
                     */

                    // Создаём ячейки и вставляем данные
                    newRow.innerHTML = `
                    <td>${data.x || 'undefined'}</td>
                    <td>${data.y || 'undefined'}</td>
                    <td>${data.r || 'undefined'}</td>
                    <td>${data.coordsStatus || 'undefined'}</td>
                    <td>${data.currentTime || 'undefined'}</td>
                    <td>${data.benchmarkTime || 'undefined'}</td>
                    `;

                    // Добавляем новую строку в таблицу
                    tableBody.appendChild(newRow);
                    });
                    return serverAnswer;

                    // Отображаем данные на странице
                    // document.getElementById("output").innerHTML = JSON.stringify(serverAnswer);
                    // return serverAnswer;
                })
                // .then(function (nextData) {
                //     // Следующая обработка (пример)
                //     console.log("Additional processing with data:", nextData);
                //     // Можно сделать что-то ещё с данными
                // })
                .catch(error => {
                    // Логируем и выводим ошибку
                    console.error('Error occurred:', error);
                    alert(`There was an error processing your request: ${error.message}`);
                });
        } else {
            Toastify({
                text: validator.getMessage(),
                className: "info",
                style: {
                    background: "linear-gradient(to right, #00b09b, #96c93d)",
                    border: "1px solid white"
                },
                offset: {
                    x: 240,
                    y: 60
                },
                position: "right",
            }).showToast();
        }
    } else {
        Toastify({
            text: "You should fill the form before submitting it :)",
            className: "info",
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
                border: "1px solid white"
            },
            offset: {
                x: 240,
                y: 60
            },
            position: "right",
        }).showToast();
    }
});


