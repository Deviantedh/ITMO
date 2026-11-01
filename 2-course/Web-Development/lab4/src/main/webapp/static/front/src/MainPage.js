import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './main_page.css';
import {useSelector} from "react-redux";
import axios from "axios";
import { SelectButton } from 'primereact/selectbutton';

const App = () => {
    const [id, setId] = useState(sessionStorage.getItem('id') || '');
    const [token, setToken] = useState(sessionStorage.getItem('token') || '');
    const [xValue, setXValue] = useState('');
    const [yValue, setYValue] = useState('');
    const [radius, setRadius] = useState(3);
    const [history, setHistory] = useState([]);
    const [points, setPoints] = useState([]);
    const canvasRef = React.useRef(null);
    const backgroundCanvasRef = React.useRef(null);
    const navigate = useNavigate();
    const radiusOptions = ['1', '2', '3', '4', '5'];
    const xOptions = ['-3', '-2', '-1', '0', '1', '2', '3', '4', '5'];
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.get(`http://localhost:8080/web_4th_lab-1.0/rest/pointChecker/checkPoint`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                params:{
                    x_cord: xValue,
                    y_cord: yValue,
                    radius: radius,
                    id: id,
                    token: token,
                }
            });


            if (response.status === 200) {
                let k=1
                const responseData = await response.data;
                const parsedData = responseData.trim().split('\n').map((line) => {
                    const [xCord, yCord, radius, requestTime, executionTime, result] = line.split('%');
                    return {
                        id: k++,
                        x: parseFloat(xCord),
                        y: parseFloat(yCord),
                        radius: parseFloat(radius),
                        requestTime,
                        executionTime: parseFloat(executionTime),
                        result: result === 'true',
                    };
                });

                points.sort((a, b) => new Date(a.requestTime) - new Date(b.requestTime));

                setHistory(parsedData);
                setPoints(parsedData.map((entry) => ({ x: entry.x, y: entry.y, result: entry.result })));
                drawFigure();
            } else {
                console.error('Failed to fetch data:', response.status);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    const handleCanvasClick = async (e) => {
        const canvas = canvasRef.current;
        const rect = canvas.getBoundingClientRect();
        const width = canvas.width;
        const height = canvas.height;
        const centerX = width / 2;
        const centerY = height / 2;

        const clickX = e.clientX - rect.left;
        const clickY = e.clientY - rect.top;

        const x = (clickX - centerX) / 50;
        const y = -(clickY - centerY) / 50;

        setXValue(x.toFixed(2));
        setYValue(y.toFixed(2));

        try {
            const response = await axios.get(`http://localhost:8080/web_4th_lab-1.0/rest/pointChecker/checkPoint`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
                params:{
                    x_cord: x,
                    y_cord: y,
                    radius: radius,
                    id: id,
                    token: token,
                }
            });


            if (response.status === 200) {
                console.log({
                    x_cord: x,
                    y_cord: y,
                    radius: radius,
                    id: id,
                    token: token,
            })
                let k=1
                const responseData = await response.data;
                const parsedData = responseData.trim().split('\n').map((line) => {
                    const [xCord, yCord, radius, requestTime, executionTime, result] = line.split('%');
                    return {
                        id: k++,
                        x: parseFloat(xCord),
                        y: parseFloat(yCord),
                        radius: parseFloat(radius),
                        requestTime,
                        executionTime: parseFloat(executionTime),
                        result: result === 'true',
                    };
                });

                points.sort((a, b) => new Date(a.requestTime) - new Date(b.requestTime));

                setHistory(parsedData);
                setPoints(parsedData.map((entry) => ({ x: entry.x, y: entry.y, result: entry.result })));
                drawFigure();
            } else {
                console.error('Failed to fetch data:', response.status);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    const drawAxes = () => {
        const canvas = backgroundCanvasRef.current;
        const ctx = canvas.getContext('2d');
        const width = canvas.width;
        const height = canvas.height;
        const centerX = width / 2;
        const centerY = height / 2;

        ctx.clearRect(0, 0, width, height);

        ctx.beginPath();
        ctx.moveTo(0, centerY);
        ctx.lineTo(width, centerY);
        ctx.moveTo(centerX, 0);
        ctx.lineTo(centerX, height);
        ctx.strokeStyle = 'black';
        ctx.stroke();

        const axisArrowSize = 10;
        ctx.beginPath();
        ctx.moveTo(width - axisArrowSize, centerY - axisArrowSize);
        ctx.lineTo(width, centerY);
        ctx.lineTo(width - axisArrowSize, centerY + axisArrowSize);
        ctx.fillStyle = 'black';
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(centerX - axisArrowSize, axisArrowSize);
        ctx.lineTo(centerX, 0);
        ctx.lineTo(centerX + axisArrowSize, axisArrowSize);
        ctx.fillStyle = 'black';
        ctx.fill();

        const step = 2.5;
        const labels = [-5, -2.5, 2.5];

        labels.forEach((label) => {
            const xPosition = centerX + label * 50;
            if (label !== 0) {
                ctx.fillText(label, xPosition, centerY + 15);
                ctx.beginPath();
                ctx.moveTo(xPosition, centerY + 5);
                ctx.lineTo(xPosition, centerY - 5);
                ctx.stroke();
            }
        });

        labels.forEach((label) => {
            const yPosition = centerY - label * 50;
            if (label !== 0) {
                ctx.fillText(label, centerX + 5, yPosition);
                ctx.beginPath();
                ctx.moveTo(centerX - 5, yPosition);
                ctx.lineTo(centerX + 5, yPosition);
                ctx.stroke();
            }
        });
    };

    const checkPoint = (x, y, r) => {
        const firstFig = (y>=0 && y<=r) && (x>=0 && x<=r);
        const secondFig = ((x*x+y*y)<=(r*r/4)) && (x<=0) && (y>=0);
        const thirdFig = (x >= -2 * (y + r / 2)) && (x <= 0) && (y <= 0);
        return firstFig || secondFig || thirdFig;
    };

    const drawFigure = () => {
        const canvas = canvasRef.current;
        const ctx = canvas.getContext('2d');
        const width = canvas.width;
        const height = canvas.height;
        const centerX = width / 2;
        const centerY = height / 2;

        ctx.clearRect(0, 0, width, height);

        ctx.fillStyle = 'rgba(0, 0, 255, 0.5)';

        const maxSquareSize = width / 2;
        const squareSize = (radius / 5) * maxSquareSize;
        const squareX = centerX ;
        const squareY = centerY - squareSize;
        ctx.fillRect(squareX, squareY, squareSize, squareSize);

        const triangleHeight = (radius / 5) * maxSquareSize;
        const triangleBase = triangleHeight / 2;
        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.lineTo(centerX , centerY + triangleBase);
        ctx.lineTo(centerX - triangleHeight, centerY);
        ctx.closePath();
        ctx.fill();

        const circleRadius = (radius / 10) * width;
        ctx.beginPath();
        ctx.arc(centerX, centerY, circleRadius / 2, 1 * Math.PI, 1.5 * Math.PI, false);
        ctx.lineTo(centerX, centerY);
        ctx.closePath();
        ctx.fill();

        points.forEach((point) => {
            const x = centerX + point.x * 50;
            const y = centerY - point.y * 50;
            ctx.fillStyle = checkPoint(point.x, point.y, radius) ? '#49e400' : 'red';
            ctx.beginPath();
            ctx.arc(x, y, 5, 0, 2 * Math.PI);
            ctx.fill();
        });
    };

    const fetchPoints = async () => {
        try {
            const response = await axios.get('http://localhost:8080/web_4th_lab-1.0/rest/pointChecker/getPoints', {
                params: {
                    id: id,
                    token: token,
                },
            });

            if (response.status === 200) {
                if(response.data !== "") {
                    let k = 0;
                    const pointsData = response.data.trim().split('\n').map((line) => {
                        const [xCord, yCord, radius, requestTime, executionTime, result] = line.split('%');
                        return {
                            id: k++,
                            x: parseFloat(xCord),
                            y: parseFloat(yCord),
                            radius: parseFloat(radius),
                            requestTime,
                            executionTime: parseFloat(executionTime),
                            result: result === 'true',
                        };
                    });

                    points.sort((a, b) => new Date(a.requestTime) - new Date(b.requestTime));

                    setHistory(pointsData);
                    setPoints(pointsData.map((point) => ({x: point.x, y: point.y, result: point.result})));
                }
            } else {
                console.error('Failed to fetch points:', response.status);
            }
        } catch (error) {
            console.error('Error fetching points:', error);
        }
    };
    const clearHistory = async () => {
        try {
            const response = await axios.get('http://localhost:8080/web_4th_lab-1.0/rest/pointChecker/clearPointsHistory', {
                params: {
                    id: id,
                    token: token,
                },
            });

            if (response.status === 200) {
                setHistory([]);
                setPoints([]);
            } else {
                console.error('Failed to fetch points:', response.status);
            }
        } catch (error) {
            console.error('Error fetching points:', error);
        }
    };
    const deleteAccount = async () => {
        try {
            const response = await axios.get('http://localhost:8080/web_4th_lab-1.0/rest/pointChecker/deleteUser', {
                params: {
                    id: id,
                    token: token,
                },
            });

            if (response.status !== 200){
                console.error('Failed to fetch points:', response.status);
            }
        } catch (error) {
            console.error('Error fetching points:', error);
        }
    };
    const buttonTemplate = (option) => (
        <div style={{ padding: '10px 20px', margin: '5px', textAlign: 'center' }}>
            {option}
        </div>
    );
    useEffect(() => {
        drawAxes();
        drawFigure();
    }, [points, radius]);

    useEffect(() => {
        fetchPoints();
    }, []);

    return (
        <div className="App">
            <div className="content">
                <div className="form-container">
                    <form onSubmit={handleSubmit}>
                        <label>
                            X:
                            <SelectButton
                                value={xValue}
                                options={xOptions}
                                onChange={(e) => setXValue(e.value)}
                                itemTemplate={buttonTemplate}
                                className="custom-select-button2"
                            />
                        </label>
                        <label>
                            Y:
                            <input
                                type="number"
                                value={yValue}
                                onChange={(e) => setYValue(e.target.value)}
                            />
                        </label>
                        <label>
                            Radius:
                            <SelectButton
                                value={radius}
                                options={radiusOptions}
                                onChange={(e) => setRadius(e.value)}
                                itemTemplate={buttonTemplate}
                                className="custom-select-button"
                            />
                        </label>
                        <label className={"evil_margin_trick"}>
                            _
                            <button type="submit">Get Result</button>
                        </label>
                        <label className={"evil_margin_trick"}>
                            _
                            <button onClick={(event) => {
                                event.preventDefault();
                                clearHistory();
                            }}>Clear points history</button>
                        </label>
                        <label className={"evil_margin_trick"}>
                            _
                            <button onClick={(event) => {
                                event.preventDefault();
                                deleteAccount();
                                navigate('/web_4th_lab-1.0/');
                            }}>delete account</button>
                        </label>
                    </form>
                    <button onClick={() => navigate('/web_4th_lab-1.0/')}>log out</button>
                </div>

                <div className="canvas-container">
                    <div style={{position: 'relative', width: '500px', height: '500px'}}>
                        <canvas
                            ref={backgroundCanvasRef}
                            width={500}
                            height={500}
                            style={{
                                position: 'absolute',
                                top: 0,
                                left: 0,
                                zIndex: 1,
                            }}
                        />
                        <canvas
                            ref={canvasRef}
                            width={500}
                            height={500}
                            onClick={handleCanvasClick}
                            style={{
                                position: 'absolute',
                                top: 0,
                                left: 0,
                                zIndex: 2,
                            }}
                        />
                    </div>
                </div>

                <div className="form-container">
                    <table>
                        <thead>
                        <tr>
                            <th>№</th>
                            <th>X</th>
                            <th>Y</th>
                            <th>R</th>
                            <th>Request Time</th>
                            <th>Execution Time</th>
                            <th>Result</th>
                        </tr>
                        </thead>
                        <tbody>
                        {history.map((entry, index) => (
                            <tr key={index}>
                                <td>{entry.id}</td>
                                <td>{entry.x}</td>
                                <td>{entry.y}</td>
                                <td>{entry.radius}</td>
                                <td>{entry.requestTime}</td>
                                <td>{entry.executionTime}</td>
                                <td>{entry.result ? 'Yes' : 'No'}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default App;
