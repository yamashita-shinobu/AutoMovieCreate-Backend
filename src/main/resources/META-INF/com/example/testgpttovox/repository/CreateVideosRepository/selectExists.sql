select
  exists
(
  select
    *
  from
    mstknr.create_videos
  where
    userid = /* userId */''
  and
    video_id = /* videoId */''
)