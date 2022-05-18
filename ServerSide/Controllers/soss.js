const Post = require("../Models/post_model");
const Sos = require("../Models/sos_model");
const User = require("../Models/user_model");

const addSos = async (req, res, next) => {

  console.log("## Add Sos");
  var email = req.body.email;
  const user = await User.findOne({ email: email });
  var description = req.body.description;
  var title = req.body.title;
  var time = req.body.time;
  var role = req.body.role;
  var category = req.body.category;
  var friendsCircle = Number(req.body.circle);

  const sos = await Sos({
    sender: user,
    title: title,
    time: time,
    description: description,
    role: role,
    friends_circle: friendsCircle,
    category: category,
    volunteers: [],
    area: '',
    address: '',
    approved_volunteer: null,
  });

  sos.save((error, newPost) => {
    if (error) {
      console.log("error");
      res.status(400).send({
        status: "fail",
        error: error.message,
      });
      console.log(error.message);
    } else {
      console.log("sos added!");
      console.log(newPost);

      res.status(200).send({
        status: "OK",
        post: newPost,
      });
    }
  });
};

// const addSos = async (req, res, next) => {
//   console.log("add new sos: " + req.body.message);
//   sender = req.user.id;
//   var message = req.body.message;
//   var title = req.body.title;
//   var time = req.body.time;
//   var role = "SOS";
//   var status = req.body.status;

//   var friends_circle = req.body.friends_circle;
//   var area = req.body.area;
//   var address = req.body.address;

//   const sos = Sos({
//     sender: sender,
//     title: title,
//     time: time,
//     message: message,
//     role: role,
//     status: status,

//     friends_circle: friends_circle,
//     area: area,
//     address: address,
//     volunteers: [],
//     approved_volunteer: null,
//   });

//   sos.save((error, newPost) => {
//     if (error) {
//       res.status(400).send({
//         status: "fail",
//         error: error.message,
//       });
//     } else {
//       res.status(200).send({
//         status: "OK",
//         post: newPost,
//       });
//     }
//   });
// };


const getSoss = async (req, res, next) => {
  Post.find({ role: "SOS" }, function (err, docs) {
    if (err) {
      console.log(err);
    } else {
      res.status(200).send(docs);
    }
  });
};


//# PARAMS
// req.params.id -sos 
const getSosVolunteers = async (req, res, next) => {

  Post.findById(req.query.id)
    .populate({
      path: "volunteers",
      model: 'User'
    })
    .exec((err, postInclude) => {
      console.log("postInclude.volunteers");
      console.log(postInclude.volunteers);

      res.status(200).send(postInclude.volunteers);
    });
};

//# PARAMS
//req.body.vol_id - user 
// req.params.id -sos 
const volunteer = async (req, res, next) => {

  const user = await User.findById(req.body.vol_id);

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "volunteers" })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: 'SOS not found in DB'

        });
      }
      if (sos.status != "OPEN") {
        console.log("## Eroor: cant apply request - status is not OPEN");
      }
      else {

        sos.volunteers.addToSet(user._id);
        sos.save(function (err) {
          if (err) {
            res.status(400).send({
              status: "fail",
              error: err.message,
              message: 'the request to help NOT saved.'
            });

          }
        });
      }
    });
}

//# PARAMS
//req.body.vol_id - user 
// req.params.id -sos 
const cancelVolunteer = async (req, res, next) => {

  console.log(" ## cancelApplyRequest");
  const user = await User.findById(req.body.user_request_id);

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "volunteers" })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: 'SOS not found in DB'

        });
      }

      for (var i = 0; i < sos.volunteers.length; i++) {
        if (sos.volunteers[i].email == user.email) {
          sos.volunteers.splice(i, 1);
        }
      }
      sos.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: 'the request to an help NOT saved.'
          });

        }

        console.log(" ## cancelApplyRequest saved");
        console.log(sos);
      });

    });

};

//# PARAMS
// req.params.id -sos 
const closeSosCancel = async (req, res, next) => {

  console.log(" ## closeSos");
  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "volunteers" })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: 'SOS not found in DB'

        });
      }

      sos.status = "CLOSE";
      var size = sos.volunteers.length;
      sos.volunteers.splice(0, size);



      sos.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: 'the request to help NOT saved.'
          });

        }

        console.log(" ## closeSos saved");
        console.log(sos);


      });

    });

};

//# PARAMS
//req.body.vol_id - user 
// req.params.id -sos 
const approveVolunteer = async (req, res, next) => {

  const user = await User.findById(req.body.vol_id);

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({
      path: "volunteers", model: 'User'
    })
    .populate({
      path: 'approved_volunteer', model: 'User'
    })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: 'SOS not found in DB'
        });
      }


      for (var i = 0; i < sos.volunteers.length; i++) {

        if (sos.volunteers[i].email == user.email) {
          sos.volunteers.splice(i, 1);
        }
      }
      sos.approved_volunteer = user._id;
      sos.status = "WAITING";

      sos.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: 'the request to help NOT saved.'
          });

        }

        console.log(" ## approveRequest saved");
        console.log(sos);


      });

    });

};


//# PARAMS
//req.body.vol_id - user 
// req.params.id -sos
const cancelApproveVolunteer = async (req, res, next) => {
  //if the post publisher want to cancel the approve OR the request sender wants to cancel the request after approve

  console.log(" ## cancelApproveRequest");
  const user = await User.findById(req.body.request_id);

  Post.findById(req.params.id)
    .where({ role: "SOS" })
    .populate({ path: "volunteers" })
    .populate({ path: 'approved_help' })
    .exec((err, sos) => {
      if (err) {
        res.status(400).send({
          status: "fail",
          error: error.message,
          message: 'SOS not found in DB'
        });
      }

      sos.status = "OPEN";
      sos.approved_help = null;

      sos.save(function (err) {
        if (err) {
          res.status(400).send({
            status: "fail",
            error: err.message,
            message: 'the request to cancelApproveRequest NOT saved.'
          });

        }

        console.log(" ## cancelApproveRequest saved");
        console.log(sos);


      });

    });

};

//Stop point 
// NEEDTO build the clisent side


module.exports = {
  addSos,
  getSoss,
  volunteer,
  approveVolunteer,
  cancelVolunteer,
  cancelApproveVolunteer,
  getSosVolunteers,
  closeSosCancel,
};
