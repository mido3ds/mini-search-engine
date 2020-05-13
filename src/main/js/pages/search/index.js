import Pagination from '@material-ui/lab/Pagination'
import qs from 'qs'
import React, { useEffect, useState } from 'react'
import { render } from 'react-dom'
import { useLocation } from "../../use-location"
import { DefaultApi } from '../../api'
import CommonSearchBar from '../../common-search-bar'
import SearchResult from '../../search-result'
import 'bootstrap/dist/css/bootstrap.min.css';
import Container from 'react-bootstrap/Container';

// This page shows:
//     common search bar (sticks on the top)
//     results of regular queries

//To be developed:
    // receiving the query that was called
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
                        setErr("No Matching Results")
                    }
                })
                .catch(error => {
                    setErr("No Matching Results")
                })
        }
    }, [q, p])

    if (err) {
        return (
            <div style = {errStyle} >
                 <h2>{err}</h2>
            </div>
           
        )
    } else {
        return (
            <div >
                {results.map((r, i) => <SearchResult r={r} key={i} />)}
                <Pagination
                    count={allPages}
                    color="primary"  
                    shape="rounded" 
                    page={currPage} 
                    onChange={(_, page) => setP(page)} 
                    style = {pageStyle}
                />
                
            </div>
        )
    }
}

const pageStyle = {
    marginTop: "30px",
    marginBottom: "20px",
    display: "flex",
    justifyContent: "center",
    alignItems: "center"
}

const errStyle = {
    marginTop: "100px",
    display: "flex",
    justifyContent: "center",
    alignItems: "center"
}

const Index = () => (
    <div style={{backgroundColor : "#F8FBFF"}}>
        <CommonSearchBar 
            oldQuery = {window.location.search}
        />
        <Results 
        />
    </div>
)

render(
    <Index />,
    document.getElementById('app-root')
)
