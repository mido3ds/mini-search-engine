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

    // ["Mahmoud Adas",
    // "Mahmoud Othman Adas",
    // "Shrek",
    // "Mahmoud Adas Again",
    // "Maybe Mahmoud Osman Adas",
    // "Adas Adas","Yup, it's 3ds ᕕ( ᐛ )ᕗ",
    // "Of course adas is the most trndy, what did you expect?",
    // "No not adas again",
    // "Adas (✿⁠´ ꒳ ` )"]

    
    // This page shows:
    //     common search bar (sticks on the top)
    //     results of regular queries
    
    //To be developed:
        //styling the background color of the nav
    
    const API = new DefaultApi()
    
    const Results = () => {
        const [results, setResults] = useState([])
        
        const [country, setQ] = useState("")
        const [err, setErr] = useState("")
    
        const { search } = useLocation()
        
        //I don't know the usage of this code!
        useEffect(() => {
            const parsed = qs.parse(search, { ignoreQueryPrefix: true })
            setQ(parsed.country)
        }, [search])
    
        useEffect(() => {
            if (country !== "") {
                API.trends(country)
                    .then(resp => {
                        if (resp.status === 200) {
                            setResults(resp.data.results)
                            setErr("")
                        } else {
                            setErr("No Trending Results")
                        }
                    })
                    .catch(error => {
                        setErr("No Trending Results")
                    })
            }
        }, [country])
        
        if (err) {
            return (
                <div style = {errStyle} >
                     <h2>{err}</h2>
                </div>
            )
        } else {
            return (
                <div >
                    <h1>ev</h1>
                    {/* {results.map((r, i) => <SearchResult r={r} image = {false} key={i} />)}
                    <Pagination
                        count={allPages}
                        color="primary"  
                        shape="rounded" 
                        page={currPage} 
                        onChange={(_, page) => setP(page)} 
                        style = {pageStyle}
                    />
                     */}
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
        
            <Results />
    )
    
    render(
        <Index />,
        document.getElementById('app-root')
    )
    