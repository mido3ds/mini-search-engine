// import Pagination from '@material-ui/lab/Pagination'
import qs from 'qs'
import React, { useEffect, useState } from 'react'
import { render } from 'react-dom'
import { useLocation } from "../../use-location"
import { DefaultApi } from '../../api'
// import CommonSearchBar from '../../common-search-bar'
// import SearchResult from '../../search-result'
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from 'react-bootstrap/Table';
import { Container } from '@material-ui/core'
import CommonSearchBar from '../../common-search-bar'


    const ev = ["Mahmoud Adas",
    "Mahmoud Othman Adas",
    "Shrek",
    "Mahmoud Adas Again",
    "Maybe Mahmoud Osman Adas",
    "Adas Adas","Yup, it's 3ds ᕕ( ᐛ )ᕗ",
    "Of course adas is the most trndy, what did you expect?",
    "No not adas again",
    "Adas (✿⁠´ ꒳ ` )"]

    
    // This page shows:
    //     common search bar (sticks on the top)
    //     results of regular queries
    
    //To be developed:
        //styling the background color of the nav
    
    const API = new DefaultApi()
    const iso31661 = require('iso-3166')



    const Results = () => {
        const [results, setResults] = useState([])
        const [country, setQ] = useState("")
        const [err, setErr] = useState("")
        const { search } = useLocation()
        const [countryName, setCountryName] = useState("")
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
                            setResults(resp.data)
                            setErr("")
                        } else {
                            setErr("No Trending Results")
                        }
                    })
                    .catch(error => {
                        setErr("No Trending Results")
                    })
            }

            if (country !== ""){
                let i = 0
                // console.log(country)
                while (i < iso31661.length)
                {
                    if (iso31661[i].alpha3 === country)
                    {
                        // console.log(iso31661[i].name)
                        setCountryName(iso31661[i].name)
                    }
                    i += 1
                }
            }

        }, [country])
        

        // console.log(country)
        // console.log(ev)

        //not error for now
        if (err) {
            return (
                <div style = {errStyle} >
                     <h2>{err}</h2>
                </div>
            )
        } else {
            return (
                <>
                    <Container maxWidth="sm" style = {tableStyle}>
                        <h1 style = {lbl}>Trending in {countryName}</h1>
                    </Container>
                    <Container maxWidth="sm" style = {tableStyle}>
                    <Table striped bordered hover >
                        <thead>
                            <tr>
                            <th>#</th>
                            <th>Name</th>
                            </tr>
                        </thead>
                        <tbody>
                            {results.map((r, i) => <TrendResult r={r} ind={i+1} key={i} />)}
                        </tbody>
                    </Table>
                    </Container>
                </>
            )
        }
    }
    
    const lbl = {
        textAlign:"center",
        fontFamily: 'Aclonica',
        fontSize: "60px",
        marginTop: "40px"
        }

    const tableStyle = {
        display: 'flex',
        justifyContent: 'center',
        marginTop : "15px"
        
    }
    
    const errStyle = {
        marginTop: "100px",
        display: "flex",
        justifyContent: "center",
        alignItems: "center"
    }
    
    const TrendResult = ({r,ind}) => {
        return (
            <tr>
            <td>{ind}</td>
            <td>{r}</td>
            </tr>
        );
    }


    const Index = () => (
        <>
        <CommonSearchBar oldQuery = ""/>
        <Results />
        </>
    )
    
    render(
        <Index />,
        document.getElementById('app-root')
    )
    