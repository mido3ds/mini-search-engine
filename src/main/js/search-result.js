import React from 'react'

const SearchResult = ({ r }) => (
    <div>
        {/* // TODO wrap in a card */}
        <a href={r.link}>
            <div>{r.title}</div>
        </a>

        <a href={r.link}>
            {r.link}<br />
        </a>

        <div>{r.snippet}</div><br />
    </div>
)

export default SearchResult;