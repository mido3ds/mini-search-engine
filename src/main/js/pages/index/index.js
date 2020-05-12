import React from 'react'
import { render } from 'react-dom'
import SearchBar from '../../search-bar'
import 'bootstrap/dist/css/bootstrap.min.css';


// this is the main page
// I'll add the main search-bar here
// and the common search-bar will be outside

const Index = () => (
    
    <div>
        <SearchBar />
    </div>
    
)

render(
    <Index />,
    document.getElementById('app-root')
)
