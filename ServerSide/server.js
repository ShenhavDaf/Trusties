const dotenv = require("dotenv").config();
const express = require("express");
const app = express();
const bodyParser = require("body-parser");
const mongoose = require("mongoose");

if (process.env.NODE_ENV == "development") {
  const swaggerUI = require("swagger-ui-express");
  const swaggerJsDoc = require("swagger-jsdoc");
  const options = {
    definition: {
      openapi: "3.0.0",
      info: {
        title: "Node demo API",
        version: "1.0.0",
        description: "A simple Express Library API",
      },
      servers: [{ url: "http://localhost:" + process.env.PORT }],
    },
    apis: ["./routes/*.js"],
  };
  const specs = swaggerJsDoc(options);
  app.use("/api-docs", swaggerUI.serve, swaggerUI.setup(specs));
}

/* Connect to mongo */
mongoose.connect(process.env.DATABASE_URL, { useNewUrlParser: true });
const db = mongoose.connection;
db.on("error", (error) => console.error(error));
db.once("open", () => console.log("Connected to mongo"));

/* body parser */

app.use(bodyParser.urlencoded({ extended: true, limit: "1mb" }));
app.use(bodyParser.json());

const port = process.env.PORT;

const indexRouter = require("./Routes/index");
const postRouter = require("./Routes/post_routes");
const authRouter = require("./Routes/auth_routes");
const userRouter = require("./Routes/user_routes");
const commentRouter = require("./Routes/comment_routes");
const sosRouter = require("./Routes/sos_routes");
const notificationRouter = require("./Routes/notification_routes");
const categoryRouter = require("./Routes/category_routes");

app.use("/", indexRouter);
app.use("/auth", authRouter);
app.use("/user", userRouter);
app.use("/post", postRouter);
app.use("/comment", commentRouter);
app.use("/sos", sosRouter);
app.use("/notification", notificationRouter);
app.use("/category", categoryRouter);

module.exports = app;
