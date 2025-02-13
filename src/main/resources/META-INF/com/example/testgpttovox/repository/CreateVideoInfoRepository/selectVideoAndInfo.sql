select
  info.video_id,
  info.title,
  info.description,
  info.tag,
  info.scenario,
  data.pc_video,
  data.mobile_video
from
  mstknr.create_video_info info
inner join
  mstknr.create_videos data
  on
  data.userid = info.userid
  and
  data.video_id = info.video_id
where
  info.userid = /* userId */''
and
  info.video_id = /* videoId  */''
