import CssBaseline from '@material-ui/core/CssBaseline'
import qs from 'qs'
import React, { useEffect, useState } from 'react'
import { render } from 'react-dom'
import { BrowserRouter as Router, Route, Switch, useLocation } from "react-router-dom"
import { DefaultApi } from './api'
import SearchBar from './searchbar'
import Pagination from '@material-ui/lab/Pagination'


const API = new DefaultApi()

const SearchResult = ({ r }) => {
    // TODO wrap in a card
    return (
        <div>
            <a href={r.link}>
                <div>{r.title}</div>
            </a>

            <a href={r.link}>
                {r.link}<br />
            </a>

            <div>{r.snippet}</div><br />
        </div>
    )
}

const SearchPage = () => {
    const [results, setResults] = useState([])
    const [currPage, setCurrPage] = useState(1)
    const [allPages, setAllPages] = useState(1)

    const [q, setQ] = useState("")
    const [p, setP] = useState(1)
    const [err, setErr] = useState("")

    const { search } = useLocation()

    useEffect(() => {
        const parsed = qs.parse(search, { ignoreQueryPrefix: true })
        setQ(parsed.q)
        setP(parsed.p)
    }, [search])

    useEffect(() => {
        if (q !== "") {
            API.query(q, p)
                .then(resp => {
                    if (resp.status === 200) {
                        setResults(resp.data.results)
                        setCurrPage(resp.data.currentPage)
                        setAllPages(resp.data.totalPages)

                        setErr("")
                    } else {
                        setErr(`error in query, resp.status=${resp.status}`)
                    }
                })
                .catch(reason => {
                    setErr(`error in query, reson=${reason}`)
                })
        }
    }, [q, p])

    const renderErr = () => (
        <h2>{err}</h2>
    )

    const renderResult = () => (
        <div>
            <p>Page {currPage}/{allPages}</p>
            {results.map((r, i) => <SearchResult r={r} key={i} />)}
            <Pagination count={allPages} shape="rounded" page={currPage} onChange={(_, page) => setP(page)} /><br />
        </div>
    )

    return (
        <div>
            {err ? renderErr() : renderResult()}
        </div>
    )
}

const App = () => {
    return (
        <div>
            <CssBaseline />
            <SearchBar />

            <Switch>
                <Route exact path="/" />

                <Route path="/search">
                    <SearchPage />
                </Route>
            </Switch>
        </div>
    )
}

render(
    <Router>
        <App />
    </Router>,
    document.getElementById('app-root')
)
