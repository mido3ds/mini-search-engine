// import Button from '@material-ui/core/Button'
import CircularProgress from '@material-ui/core/CircularProgress'
import TextField from '@material-ui/core/TextField'
import Autocomplete from '@material-ui/lab/Autocomplete'
import matchSorter from 'match-sorter'
import SettingsVoiceRoundedIcon from '@material-ui/icons/SettingsVoiceRounded';
import SearchRoundedIcon from '@material-ui/icons/SearchRounded';
import PhotoLibraryRoundedIcon from '@material-ui/icons/PhotoLibraryRounded';
import React, { useEffect, useState } from 'react'
import { DefaultApi } from './api'
// Bootstrap
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
// import {Button, Form, FormControl, Navbar} from 'react-bootstrap';
import '../App.css';
import SpeechRecognition from 'react-speech-recognition'
import PropTypes from "prop-types";


const API = new DefaultApi()

const propTypes = {
    // Props injected by SpeechRecognition
    transcript: PropTypes.string,
    resetTranscript: PropTypes.func,
    startListening : PropTypes.func,
    stopListening: PropTypes.func,
    listening : PropTypes.bool,
    browserSupportsSpeechRecognition: PropTypes.bool
  }


const CommonSearchBar = ({
    transcript,
    resetTranscript,
    stopListening,
    browserSupportsSpeechRecognition,
    startListening,
    oldQuery
    }) => {
    const [disabled, setDisabled] = useState(true)
    const [searchCursor, setSearchCursor] = useState("not-allowed")
    const [micDisabled, setMicDisabled] = useState("disabled")
    const [query, setQuery] = useState("")
    const [open, setOpen] = useState(false)
    const [options, setOptions] = useState([])
    const [loading, setLoading] = useState(false)

    if (!browserSupportsSpeechRecognition) {
        return null;
      }

    
    
    useEffect(() => {
        let active = true
        if (query !== "") {
            setLoading(true)
            API.complete(query).then(resp => {
                if (resp.status === 200 && active) {
                    setOptions(resp.data)
                }
            }).finally(() => {
                setLoading(false)
            })
        }

        return () => {
            active = false
        }
    }, [query])

    useEffect(() => {
        if (!open) {
            setOptions([])
        }
    }, [open])

    
    useEffect(() => {
        //This effect is run once..and never again
        if (oldQuery !== "") {
            //removing "?q="
            let val = oldQuery.slice(3, oldQuery.length)
            val.replace("%20", " ");
            val.replace("%27", "'");
            val.replace("%22", '"');            
            setQuery(val)
            oldQuery = ""
            // console.log(oldQuery)
        }
       
    }, [oldQuery])
    

    const onClick = () => {
        if (query) {
            window.location = `/search?q=${query}`
        }
    }
    
    useEffect(() => {
        if (query === "") {
            setDisabled(true)
            setSearchCursor ("not-allowed")
            setOptions([])
        } else if (disabled) {
            setDisabled(false)
            setSearchCursor("pointer")
        }
    }, [query])


    const onVoiceClick = (e) => {

        if (micDisabled === "disabled")
            //Recording
            {
                resetTranscript()
                startListening()
                setMicDisabled("primary")
            }
        else
            //Not recording
            {   
                setTimeout(() => {  
                    let newQ = query;
                    if (query !== "" && query[query.length-1] !== ' ' && transcript !== "")
                        newQ += ' ';
                    newQ = newQ + transcript;
                    setQuery (newQ)
                    setMicDisabled("disabled")
                    stopListening();
                }, 250);
                
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

    const showQ = ()=>{
        console.log(query)
    }
    
    return (
    <Container style={{display: 'flex', justifyContent: 'center'}} >
    <div onKeyPress={onKey} style={{marginTop: '1%',marginBottom: '1%' ,backgroundColor : "#F8FBFF"}} >
            <Navbar style = {nav} fixed="top" className="navbar navbar-light bg-light"> 
            
            <span style = {lbl} className="navbar-brand mb-0 h1">Mini Search Engine</span>
            <form noValidate autoComplete="off" style={{ display: "flex" }}>
                <Autocomplete
                    //INFO
                    id="autocomplete"
                    //Added this because the clear button is not clearing the query (ie the textField)
                    disableClearable = {true}
                    autoSelect={true}
                    //Style
                    style={{ width: "800px" }}
                    //Methods
                    open={open}
                    onOpen={() => {
                        setOpen(true)
                    }}
                    onClose={(_, reason) => {
                        if (reason === "select-option") {
                            onClick()
                        }else{
                            if (query === "") {
                                setSearchCursor ("not-allowed")
                            } else if (searchCursor === "not-allowed") {
                                setSearchCursor("pointer")
                            }
                        }
                        setOpen(false)
                    }}
                    //Options
                    getOptionSelected={(option, value) => option === value}
                    filterOptions={(options, { inputValue }) => matchSorter(options, inputValue)}
                    getOptionLabel={option => option}
                    options={options}
                    //Loading
                    loading={loading}
                    freeSolo = {true}
                    value={query}
                    renderInput={params => (
                        <TextField
                            style = {{marginLeft: "10px"}}
                            {...params}
                            size="small"
                            id="outlined-search"
                            type="search"
                            onChange={onInputChange}
                            variant="outlined"
                            label="Search"
                            InputProps={{
                                ...params.InputProps,
                                endAdornment: ( 
                                    <React.Fragment>
                                        {loading ? <CircularProgress color="inherit" size={20} /> : null}
                                        {params.InputProps.endAdornment}
                                    </React.Fragment>
                                ),
                            }}
                        />
                    )}
                />
                <SettingsVoiceRoundedIcon 
                    color= {micDisabled}
                    style = {{cursor: "pointer", marginLeft:"10px"}}
                    fontSize = "large" 
                    onClick={onVoiceClick}
                />

                <PhotoLibraryRoundedIcon
                    color="primary" 
                    fontSize = "large" 
                    onClick = {showQ}
                    style = {{cursor: searchCursor}}
                />
                
                <SearchRoundedIcon 
                    onClick={onClick}
                    color="primary" 
                    fontSize = "large"
                    style = {{cursor: searchCursor}}
                 />
                
            </form>
            <br />
            </Navbar>
        </div>
        
    </Container>
    )
}



    // I'll Leave all CSS classes here for now
    // When finished with all pages
    // will gather the common files into one App.css
    const lbl = {
        textAlign:"left",
        fontFamily: 'Aguafina Script',
        fontSize: "30px"

    }

    const nav = {
        display: "inline-flex",
        overflow: "hidden",
        maxWidth: "75%",
        marginLeft: '10%',
        padding: "10px",
        borderRadius: "35px",
        marginBottom: "10px"    
    }

    const voiceOptions = {
        autoStart: false,
        continuous: true,
      }
    

      CommonSearchBar.propTypes = propTypes;
export default SpeechRecognition(voiceOptions)(CommonSearchBar)