import React, { useEffect, useState } from 'react'
import { render } from 'react-dom'
import {
    BrowserRouter as Router,
    Switch, Route, Redirect, useLocation
} from "react-router-dom"
import qs from 'qs'

import { DefaultApi } from './api'

const api = new DefaultApi()

const SearchResult = ({ r }) => {
    return (
        <div>
            <a href={r.link}>
                <div>{r.title}</div>
                {r.link}<br />
            </a>
            <div>{r.snippet}</div><br />
        </div>
    )
}

const QueryPage = () => {
    const [results, setResults] = useState([])

    const { q, p } = qs.parse(useLocation().search, { ignoreQueryPrefix: true })

    useEffect(() => {
        api.query(q, p).then(resp => {
            if (resp.status === 200) {
                setResults(resp.data)
            } else {
                console.error(`error in query, resp.status={resp.status}`)
            }
        })
    }, [])

    return (
        <div>
            {results.map((r, i) => <SearchResult r={r} key={i} />)}
        </div>
    )
}

const HomePage = () => {
    return (
        // TODO: search bar
        <Redirect to="/query?q=ad" />
    )
}

const App = () => {
    return (
        <Router>
            {/* TODO: HEADER */}
            <div>HEADER</div>

            <Switch>
                <Route path="/query">
                    <QueryPage />
                </Route>

                <Route path="/">
                    <HomePage />
                </Route>
            </Switch>

            {/* TODO FOOTER */}
            <div>FOOTER</div>
        </Router>
    )
}

render(
    <App />,
    document.getElementById('app-root')
)
