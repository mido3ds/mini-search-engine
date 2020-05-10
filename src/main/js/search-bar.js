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
import Row       from 'react-bootstrap/Row';
import Col       from 'react-bootstrap/Col';
import {Button, Form, FormControl, Navbar} from 'react-bootstrap';
import '../App.css';

const API = new DefaultApi()

const SearchBar = () => {
    const [disabled, setDisabled] = useState(true)
    const [searchCursor, setSearchCursor] = useState("not-allowed")
    const [micDisabled, setMicDisabled] = useState("disabled")
    const [query, setQuery] = useState("")
    const [open, setOpen] = useState(false)
    const [options, setOptions] = useState([])
    const [loading, setLoading] = useState(false)

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

    const onClick = () => {
        if (query) {
            window.location = `/search?q=${query}`
        }
    }

    const onVoiceClick = (e) => {

        if (micDisabled === "disabled")
            //Recording
            setMicDisabled("primary")
        else
            //Not recording
            setMicDisabled("disabled")

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
            setSearchCursor ("not-allowed")
            setOptions([])
        } else if (disabled) {
            setDisabled(false)
            setSearchCursor("pointer")
        }
    }, [query])

    return (
    <div className = "bg" >
    <Container style={{display: 'flex', justifyContent: 'center'}} >
    
    <div onKeyPress={onKey} style={{marginTop: '200px'}} >
            <h1 className = "lbl">Mini Search Engine</h1>
            <Navbar  className = "nav" bg="light" >
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
                    renderInput={params => (
                        <TextField
                            
                            {...params}
                            size="small"
                            id="search-input"
                            onChange={onInputChange}
                            variant="outlined"
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
                fontSize = "large" 
                onClick={onVoiceClick}
                />
                {/* <PhotoCameraRoundedIcon color="primary" 
                fontSize = "large" /> */}
                
                <SearchRoundedIcon 
                onClick={onClick}
                disabled={disabled}
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

// const srch = {
    
    
// };

export default SearchBar;