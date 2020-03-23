import React, { useEffect, useState } from 'react'
import { render } from 'react-dom'
import {
    BrowserRouter as Router,
    Switch, Route, useLocation, useHistory
} from "react-router-dom"

import qs from 'qs'
import Button from '@material-ui/core/Button'
import TextField from '@material-ui/core/TextField'
import CssBaseline from '@material-ui/core/CssBaseline'

import { DefaultApi } from './api'

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
            <p>{currPage}/{allPages}</p>
            {results.map((r, i) => <SearchResult r={r} key={i} />)}
        </div>
    )

    return (
        <div>
            {err ? renderErr() : renderResult()}
        </div>
    )
}

const SearchBar = () => {
    const [query, setQuery] = useState("")
    const [disabled, setDisabled] = useState(true)
    const history = useHistory()

    const onClick = () => {
        if (query) {
            history.push(`/search?q=${query}`)
        }
    }

    const onKey = (e) => {
        if (e.key === 'Enter') {
            onClick()
            e.preventDefault()
        }
    }

    const onInputChange = (event) => {
        setQuery(event.target.value)
    }

    useEffect(() => {
        if (query === "") {
            setDisabled(true)
        } else if (disabled) {
            setDisabled(false)
        }
    }, [query])

    // TODO: add completion support

    return (
        <div onKeyPress={onKey}>
            <h1>Mini Search Engine</h1>
            <form noValidate autoComplete="off" style={{ display: "flex" }}>
                <TextField id="search-input" label="" variant="outlined" size="small" onChange={onInputChange} />

                <Button variant="outlined" color="primary" disableElevation onClick={onClick} disabled={disabled}>
                    Search
                    </Button>
            </form>
            <br />
        </div>
    )
}

// TODO: add paging support

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
