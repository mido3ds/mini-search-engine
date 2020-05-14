import CircularProgress from '@material-ui/core/CircularProgress'
import TextField from '@material-ui/core/TextField'
import Button from '@material-ui/core/Button';
import Autocomplete from '@material-ui/lab/Autocomplete'
import matchSorter from 'match-sorter'
import SettingsVoiceRoundedIcon from '@material-ui/icons/SettingsVoiceRounded';
import SearchRoundedIcon from '@material-ui/icons/SearchRounded';
import WhatshotIcon from '@material-ui/icons/Whatshot';
import PhotoLibraryRoundedIcon from '@material-ui/icons/PhotoLibraryRounded';
import React, { useEffect, useState } from 'react'
import { DefaultApi } from './api'
// Bootstrap
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Form from 'react-bootstrap/Form';

// import '../App.css';
import SpeechRecognition from 'react-speech-recognition'
import PropTypes from "prop-types";
import { makeStyles } from '@material-ui/core/styles';
// import Tooltip from '@material-ui/core/Tooltip';


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
    //the current state of sow trends cursor
    //Current state of TrendButton
    const [trendDisabled, setTrendDisabled] = useState(true)
    //modified value of the Trend Input field
    const [trendValue, setTrendValue] = useState("")
    //comparable value
    const [trendFixedValue, setTrendFixedValue] = useState("")
    //the current state of mic
    const [micDisabled, setMicDisabled] = useState("disabled")
    //main query, also the data value on the main search bar text field
    const [query, setQuery] = useState("")
    //currently selected Trend
    const [trend, setTrend] = useState("")
    //state of the autocomplete
    const [open, setOpen] = useState(false)
    //options to be displayed
    const [options, setOptions] = useState([])
    //the state of the text field
    const [loading, setLoading] = useState(false)
    


    //For country selector
    const classes = useStyles();
    
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

    //a Hook used to decide the current state of the trendsButton
    useEffect(() => {
        if (trend === "" || trend === undefined || trendValue !== trendFixedValue) {
            setTrendDisabled (true)
            // setTrendsCursor ("not-allowed")
        } else {
            setTrendDisabled (false)
            // setTrendsCursor("pointer")
        }
    }, [trend,trendValue])

    
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

    // let history = useHistory();
    const navigateHome = () => {
        window.location = window.location.origin;
    }
    

    const onTrendChange = (event, newValue) => {
        // console.log(newValue)
        setTrendValue (newValue.name)
        setTrendFixedValue (newValue.name)
        setTrend (newValue.alpha3)
    }
    //navigates us to the image search
    const onImageClick = ()=>{
        window.location = `/images?q=${query}`
    }
    
    //navigates to the trends page
    const trendsClick = ()=>{
        window.location = `/trends?country=${trend}`
    }

    const iso31661 = require('iso-3166')
 
    //Main style returned
    return (
    <div style = {bg} >
    <Container style={{display: 'flex', justifyContent: 'center'}} >
    <div onKeyPress={onKey} style={{marginTop: '200px'}} >
            <h1 onClick = {navigateHome} style = {lbl}>Mini Search Engine</h1>
            <Nav  style = {nav} bg="light" className="flex-column">
            <Nav.Item>
            <Form noValidate autoComplete="off" style={{ display: "flex" }}>
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
                 </Form>
                </Nav.Item>
                
                <Nav.Item>
                <Form noValidate autoComplete="off" style={{ display: "flex" }}>
                <Autocomplete
                    id="country-select-demo"
                    style={{ width: 270, 
                            display: 'flex', 
                            justifyContent: 'center',
                            marginRight: "5%",
                            marginLeft: "1%",
                            marginTop: "10px"
                        }}
                    options={iso31661}
                    classes={{
                        option: classes.option,
                    }}

                    inputValue={trendValue}
                    onInputChange={(event, newInputValue) => {
                    setTrendValue(newInputValue);
                    }}

                    onChange={onTrendChange}
                    autoHighlight

                    getOptionLabel={(options) => options.name}
                    renderOption={(options) => (
                        <React.Fragment>
                            <span>{options.alpha3}</span>
                            {options.name} ({options.alpha3})
                        </React.Fragment>
                    )}
                    renderInput={(params) => (
                        <TextField
                        {...params}
                        size="small"
                        label="Choose a country for Trends"
                        variant="outlined"
                        inputProps={{
                            ...params.inputProps,
                            autoComplete: 'new-password', // disable autocomplete and autofill
                        }}
                        />
                    )}
                />
                {/* <Tooltip title="Select a Country" placement="bottom-end"> */}
                <Button size="small" 
                        variant="contained" 
                        color="primary" 
                        disabled = {trendDisabled}
                        style = {{marginTop: "10px"}}
                        startIcon={<WhatshotIcon />}
                        onClick = {trendsClick}
                        >
                    Show Trends
                </Button>
                {/* </Tooltip> */}
                </Form>
                </Nav.Item>
            </Nav>
            
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
        fontSize: "90px",
        cursor: "pointer"
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
    
    const useStyles = makeStyles({
    option: {
        fontSize: 15,
        '& > span': {
        marginRight: 10,
        fontSize: 18
        },
    },
    });


    
    SearchBar.propTypes = propTypes;
    
export default SpeechRecognition(voiceOptions)(SearchBar)