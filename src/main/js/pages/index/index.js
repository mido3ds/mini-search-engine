import React from 'react'
import { render } from 'react-dom'
import SearchBar from '../../search-bar'
import {Container } from 'react-bootstrap';
const Index = () => (
    <div>
        <SearchBar />
    </div>
    
)

render(
    <Index />,
    document.getElementById('app-root')
)
