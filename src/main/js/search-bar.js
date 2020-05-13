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
// import '../App.css';
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


const SearchBar = ({
    //Props used in speech recognition
    transcript,
    resetTranscript,
    stopListening,
    browserSupportsSpeechRecognition,
    startListening
    }) => {
    /**
     * States used in main search bar
     */
    
    //disabled: bool to know if search/image icons are available or not
    const [disabled, setDisabled] = useState(true)
    //the current state of search cursor
    const [searchCursor, setSearchCursor] = useState("not-allowed")
    //the current state of mic
    const [micDisabled, setMicDisabled] = useState("disabled")
    //main query, also the data value on the main search bar text field
    const [query, setQuery] = useState("")
    //state of the autocomplete
    const [open, setOpen] = useState(false)
    //options to be displayed
    const [options, setOptions] = useState([])
    //the state of the text field
    const [loading, setLoading] = useState(false)

    //before anything, ig the browser does not support speech recognition
    //it won't work
    if (!browserSupportsSpeechRecognition) {
        return (
            <div>
                <h1>Sorry, you must use Google Chrome, otherwise spech recognition will not work</h1>
                <p>Sorry in smaller font</p>
            </div>
        );
      }
    
    //this Hook is used to get options available
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
    
    //function used to act when search icon is clicked
    const onClick = () => {
        if (query) {
            window.location = `/search?q=${query}`
        }
    }
    
    //a Hook function used to enable/disable the search icon and image icon as well
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


    //function used to control the spech recognition
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
                    console.log("World!"); 
                }, 250);
                
            } 

    }

    //when a key is hit, check if it's an Enter
    const onKey = (e) => {
        if (e.key === 'Enter') {
            onClick()
            e.preventDefault()
        }
    }

    //when an input occur, update the query
    const onInputChange = (event) => {
        setQuery(event.target.value)
    }

    //navigates us to the image search
    const onImageClick = ()=>{
        window.location = `/images?q=${query}`
    }
    
    //Main style returned
    return (
    <div style = {bg} >
    <Container style={{display: 'flex', justifyContent: 'center'}} >
    <div onKeyPress={onKey} style={{marginTop: '200px'}} >
            <h1 style = {lbl}>Mini Search Engine</h1>
            <Navbar  style = {nav} bg="light" >
            <form noValidate autoComplete="off" style={{ display: "flex" }}>
                <Autocomplete
                    //INFO
                    id="autocomplete"
                    //Added this because the clear button is not clearing the query (ie the textField)
                    disableClearable = {true}
                    autoSelect={true}
                    //Style
                    style={{ width: 800 }}
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
                    onClick = {onImageClick}
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
export default SpeechRecognition(voiceOptions)(SearchBar)