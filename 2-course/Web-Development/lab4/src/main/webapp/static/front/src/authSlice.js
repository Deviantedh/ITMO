import { createSlice } from '@reduxjs/toolkit';

const initialState = {
    isAuthenticated: false,
    id: null,
    token: null,
    error: null,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        loginSuccess(state, action) {
            state.isAuthenticated = true;
            state.id = action.payload.id;
            state.token = action.payload.token;
            state.error = null;
        },
        loginFailure(state, action) {
            state.isAuthenticated = false;
            state.id = null;
            state.token = null;
            state.error = action.payload;
        },
        logout(state) {
            state.isAuthenticated = false;
            state.id = null;
            state.token = null;
            state.error = null;
        },
    },
});

export const { loginSuccess, loginFailure, logout } = authSlice.actions;

export default authSlice.reducer;
