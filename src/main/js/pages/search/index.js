import Pagination from '@material-ui/lab/Pagination'
import qs from 'qs'
import React, { useEffect, useState } from 'react'
import { render } from 'react-dom'
import { useLocation } from "../../use-location"
import { DefaultApi } from '../../api'
import SearchBar from '../../search-bar'
import SearchResult from '../../search-result'

const API = new DefaultApi()

const Results = () => {
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
                    setErr(`error in query, reason=${reason}`)
                })
        }
    }, [q, p])

    if (err) {
        return (
            <h2>{err}</h2>
        )
    } else {
        return (
            <div>
                <p>Page {currPage}/{allPages}</p>
                {results.map((r, i) => <SearchResult r={r} key={i} />)}
                <Pagination count={allPages} shape="rounded" page={currPage} onChange={(_, page) => setP(page)} /><br />
            </div>
        )
    }
}

const Index = () => (
    <div>
        <SearchBar />
        <Results />
    </div>
)

render(
    <Index />,
    document.getElementById('app-root')
)