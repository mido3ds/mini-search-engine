import React from 'react'
import { render } from 'react-dom'
import SearchBar from '../../search-bar'

const Index = () => (
    <div>
        <SearchBar />
    </div>
)

render(
    <Index />,
    document.getElementById('app-root')
)
