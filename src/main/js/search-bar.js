// import Button from '@material-ui/core/Button'
import CircularProgress from '@material-ui/core/CircularProgress'
import TextField from '@material-ui/core/TextField'
import Autocomplete from '@material-ui/lab/Autocomplete'
import matchSorter from 'match-sorter'
import SettingsVoiceRoundedIcon from '@material-ui/icons/SettingsVoiceRounded';
import SearchRoundedIcon from '@material-ui/icons/SearchRounded';
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
    // lang: PropTypes.string,
    browserSupportsSpeechRecognition: PropTypes.bool
  }


const SearchBar = ({
    transcript,
    resetTranscript,
    stopListening,
    browserSupportsSpeechRecognition,
    startListening
    // lang
    }) => {
    const [disabled, setDisabled] = useState(true)
    const [searchCursor, setSearchCursor] = useState("not-allowed")
    const [micDisabled, setMicDisabled] = useState("disabled")
    const [query, setQuery] = useState("")
    const [value, setValue] = useState("")
    const [open, setOpen] = useState(false)
    const [options, setOptions] = useState([])
    const [loading, setLoading] = useState(false)

    if (!browserSupportsSpeechRecognition) {
        return null;
      }
    // var myLang = mySpeechRecognition.lang;
    // lang = 'en-US';
    
    
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

    // useEffect(() => {
    //     if (query !== "") {
    //         setValue(query)
    //     }
    // }, [value])

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
                // resetTranscript()
                startListening()
                console.log()
                setMicDisabled("primary")
            }
        else
            //Not recording
            {
                setQuery (transcript)
                setMicDisabled("disabled") 
                console.log(query)
                setValue(query)
                stopListening()
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

    
    return (
    <div style = {bg} >
    <Container style={{display: 'flex', justifyContent: 'center'}} >
    
    <div onKeyPress={onKey} style={{marginTop: '200px'}} >
            <h1 style = {lbl}>Mini Search Engine</h1>
            <Navbar  style = {nav} bg="light" >
            <form noValidate autoComplete="off" style={{ display: "flex" }}>
                <Autocomplete
                    id="autocomplete"
                    style={{ width: 800 }}
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
                    //Added this because the clear button is not clearing the query (ie the textField)
                    disableClearable = {true}
                    autoSelect={true}
                    getOptionSelected={(option, value) => option === value}
                    filterOptions={(options, { inputValue }) => matchSorter(options, inputValue)}
                    getOptionLabel={option => option}
                    options={options}
                    loading={loading}
                    freeSolo
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

                {/* <Button 
                    variant="outline-info" 
                    // color="primary"
                    // disableElevation
                    onClick={onClick}
                    disabled={disabled} 
                    style={{ marginLeft: "10px"}}>
                    Search
                </Button> */}
                
                <SettingsVoiceRoundedIcon 
                
                color= {micDisabled}
                style = {{cursor: "pointer", marginLeft:"10px"}}
                fontSize = "large" 
                onClick={onVoiceClick}
                />

                {/* <PhotoCameraRoundedIcon color="primary" 
                fontSize = "large" /> */}
                
                <SearchRoundedIcon 
                onClick={onClick}
                // disabled={disabled}
                color="primary" 
                fontSize = "large"
                // className = "srch"
                style = {{cursor: searchCursor}}
                 />
                
            </form>
            <br />
            </Navbar>
        </div>
        
    </Container>
    </div>
    )
}



    // I'll Leave all CSS classes here for now
    // When finished with all pages
    // will gather the common files into one App.css
    const lbl = {
        textAlign:"center",
        marginBottom: "50px",
        fontFamily: 'Aguafina Script',
        fontSize: "90px"

    }

    const nav = {

        display: "inline-flex",
        position: "relative",
        overflow: "hidden",
        maxWidth: "100%",
        backgroundColor: "#fff",
        padding: "10px",
        borderRadius: "35px",
        boxShadow: "0 10px 40px rgba(18, 18, 19, 0.8)"
    }

    const bg = {
        backgroundImage: `url("https://mdbootstrap.com/img/Photos/Horizontal/Nature/full page/img(20).jpg")`,
        // backgroundImage: `url("http://mdbootstrap.com/img/Photos/Others/images/91.jpg")`,
        height: "100vh",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        backgroundSize: "cover"
    }
    const voiceOptions = {
        autoStart: false,
        continuous: true

      }
    

    SearchBar.propTypes = propTypes;
// export default SearchBar;
export default SpeechRecognition(voiceOptions)(SearchBar)
// export default SpeechRecognition(SearchBar)