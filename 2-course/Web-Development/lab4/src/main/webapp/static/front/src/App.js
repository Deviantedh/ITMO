import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import MainPage from './MainPage';
import AuthForm from "./form";

const App = () => {
    return (
        <BrowserRouter>
            <header>Носов Георгий Иванович P3232 лаб 4</header>
            <main>
            <Routes>
                <Route path="/web_4th_lab-1.0/" element={<AuthForm />} />
                <Route path="/web_4th_lab-1.0/main_page" element={<MainPage />} />
                <Route path="*" element={<AuthForm />} />
            </Routes>
            </main>
            <footer></footer>
        </BrowserRouter>
    );
};

export default App;