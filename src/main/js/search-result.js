import React, { useEffect, useState } from 'react'


import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

// const [title, setTitle] = useState("")
// const [body, setBody] = useState("")

//Common styling
const cardStyle = {
    marginRight: "25%",
    marginTop: "60px",
    marginLeft: "15%",
    marginBottom: "-20px",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    position: "relative",
    overflow: "hidden",
    maxWidth: "100%"
}   

const cardStyle2 = {
  marginRight: "60%",
  marginTop: "60px",
  marginLeft: "15%",
  marginBottom: "-20px",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  position: "relative",
  overflow: "hidden",
  maxWidth: "100%"
}   


const SearchResult = ({ r, image }) => {

    if (!image){
      return (  
        <Card style = {cardStyle} >
          <CardActionArea >
            <CardContent >
            <a href={r.link}>
                <Typography gutterBottom variant="h5" component="h2">
                    {r.title.length < 60 ? r.title : r.title.slice(0,55) +"....."}
                    </Typography>   
            </a>
              <Typography variant="body2" color="textSecondary" component="p">
                {r.snippet.length < 300 ? r.snippet : r.snippet.slice(0,295) +"....."}
              </Typography>
            </CardContent>
          </CardActionArea>
          
        </Card>
      );
    }else{
      return (  
        <Card style = {cardStyle2}>
          <CardActionArea >
            <CardContent >
            <a href={r.link}>
                <Typography gutterBottom variant="h5" component="h2">
                    {r.title.length < 60 ? r.title : r.title.slice(0,55) +"....."}
                    </Typography>   
            </a>
            </CardContent>
          </CardActionArea>
        </Card>
      );
    }
}

export default SearchResult;